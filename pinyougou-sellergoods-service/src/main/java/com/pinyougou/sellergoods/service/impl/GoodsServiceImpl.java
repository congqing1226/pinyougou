package com.pinyougou.sellergoods.service.impl;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper descMapper;

	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private JmsTemplate jmsTemplate;

	/**
	 * 消息发送目的地 (保存)
	 */
	@Autowired
	private Destination topicPageAndSolrDestination;


	/**
	 * 消息发送目的地 (删除)
	 */
	@Autowired
	private Destination queueSolrDeleteDestination;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {

		//添加商品
		TbGoods tbGoods = goods.getGoods();
		tbGoods.setAuditStatus("0");
		goodsMapper.insert(tbGoods);

		//添加商品详情
		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
		goodsDesc.setGoodsId(tbGoods.getId());
		descMapper.insert(goodsDesc);

		/*
		 * 向Item表中添加 SKU 判断是否启用
		 *
		 */
		if("1".equals(goods.getGoods().getIsEnableSpec())){

			/*
			 * 获取SKU信息  $scope.entity.skuList = list;
			 *
			 */
			List<Map> skuList = goods.getSkuList();

			//遍历 skuList
			for(Map map : skuList){
				TbItem item = new TbItem();

				/**
				 *
				 *生成SKU商品
				 */

				String title = goods.getGoods().getGoodsName();

				/**
				 *	var list = [{spec:{},price:0,stockCount:99999}];
				 * 	规格列表	spec:{} : [{"attributeName":规格名称,"attributeValue":["规格选项 1","规格选项 2"]}]
				 */
				Map<String,String> specMap =(Map) map.get("spec");

				for(String strKey : specMap.keySet()){
					//根据获取key 获取规格项,与 title进行拼接
					title += " " + specMap.get(strKey);
				}

				String specJson = JSON.toJSONString(specMap);

				item.setSpec(specJson);

				//标题
				item.setTitle(title);

				//价格
				item.setPrice(new BigDecimal((String) map.get("price")));

				//设置图片的路径
				String itemImages = goods.getGoodsDesc().getItemImages();
				List<Map> list = JSON.parseArray(itemImages,Map.class);
				if(null != list && list.size()>0){
					// [{"color":"粉色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXq2AFIs5AAgawLS1G5Y004.jpg"}
					item.setImage((String) list.get(0).get("url"));
				}

				//商品分类ID 第三级分类ID
				item.setCategoryid(goods.getGoods().getCategory3Id());

				//商品分类的名称, 要通过ID进行查询
				TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
				item.setCategory(itemCat.getName());

				//添加时间 & 更新时间
				item.setCreateTime(new Date());
				item.setUpdateTime(new Date());



				//String 库存
				if(map.get("stockCount") instanceof String){
					item.setStockCount(Integer.parseInt((String) map.get("stockCount")));
					System.out.println("String ...... ");
				}

				//integer 库存
				if(map.get("stockCount") instanceof Integer){
					item.setStockCount((Integer) map.get("stockCount"));
				}

				//状态
				item.setStatus((String) map.get("status"));

				//是否默认
				item.setIsDefault((String) map.get("isDefault"));

				//商家ID
				item.setSeller(goods.getGoods().getSellerId());

				//商品ID
				item.setGoodsId(goods.getGoods().getId());

				itemMapper.insert(item);
			}
		//未启用 规格情况
		}else{
			TbItem item = new TbItem();
			item.setTitle(goods.getGoods().getGoodsName());
			item.setPrice(goods.getGoods().getPrice());
			item.setStockCount(99999);
			item.setStatus("1");
			item.setIsDefault("1");
			item.setSellerId(goods.getGoods().getSellerId());
			item.setGoodsId(goods.getGoods().getId());

			itemMapper.insert(item);
		}

	}


	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){

		//修改之后,改状态为 未审核状态
		goods.getGoods().setAuditStatus("0");

		//保存商品数据
		goodsMapper.updateByPrimaryKey(goods.getGoods());

		//保存商品扩展表数据
		descMapper.updateByPrimaryKey(goods.getGoodsDesc());

		//判断是否启用
		if("1".equals(goods.getGoods().getIsEnableSpec())){

			//启用规格, 获取规格列表
			List<Map> skuList = goods.getSkuList();

			for(Map map : skuList){
				//得到当前商品的规格信息
				Map<String, String> specMap =(Map<String, String>) map.get("spec");

				//获取SKU
				TbItem item = getItemBySpecMap(specMap, goods.getGoods().getId());

				//根据规格数据信息, 查询Item 有则更新,没有则插入
				if(item == null){
					item=new TbItem();
					//生成SKU商品名称
					String title=goods.getGoods().getGoodsName();

					for(String specKey: specMap.keySet()){
						title+=" "+specMap.get(specKey);
					}
					//标题
					item.setTitle(title);

					if(map.get("price") instanceof String){
						//价格
						item.setPrice(new BigDecimal( (String)map.get("price")  ));
					}
					if(map.get("price") instanceof BigDecimal){
						//价格
						item.setPrice( (BigDecimal)map.get("price")  );
					}

					if( map.get("stockCount") instanceof String ){
						//库存
						item.setStockCount(  Integer.parseInt( (String) map.get("stockCount")));
						System.out.println("String...");
					}
					if( map.get("stockCount") instanceof Integer ){
						//库存
						item.setStockCount(  (Integer) map.get("stockCount"));
						System.out.println("Integer...");
					}
					//状态
					item.setStatus((String)map.get("status"));
					//是否默认
					item.setIsDefault((String)map.get("isDefault"));
					//商家ID
					item.setSellerId(goods.getGoods().getSellerId());
					//商品ID
					item.setGoodsId(goods.getGoods().getId());

					itemMapper.insert(item);

				}else{
					//如果存在item ，修改
					if(map.get("price") instanceof String){
						item.setPrice(new BigDecimal( (String)map.get("price")  ));
					}
					if(map.get("price") instanceof BigDecimal){
						item.setPrice( (BigDecimal)map.get("price")  );
					}

					if( map.get("stockCount") instanceof String ){
						item.setStockCount(  Integer.parseInt( (String) map.get("stockCount")));
						System.out.println("String...");
					}
					if( map.get("stockCount") instanceof Integer ){
						item.setStockCount(  (Integer) map.get("stockCount"));
						System.out.println("Integer...");
					}

					item.setStatus((String)map.get("status"));
					item.setIsDefault((String)map.get("isDefault"));

					itemMapper.updateByPrimaryKey(item);

				}

			}

		}else{ //如果不启用规格

			TbItemExample example = new TbItemExample();
			TbItemExample.Criteria criteria = example.createCriteria();
			/**
			 * 条件商品名称: title中没有规格信息
			 */
			criteria.andTitleEqualTo(goods.getGoods().getGoodsName());
			List<TbItem> itemList = itemMapper.selectByExample(example);

			if(itemList.size() >0){
				TbItem item = itemList.get(0);
				item.setTitle(goods.getGoods().getGoodsName());
				item.setPrice(goods.getGoods().getPrice());
				item.setStockCount(99999);
				item.setStatus("1");
				item.setIsDefault("1");
				itemMapper.updateByPrimaryKey(item);

			}else{
				TbItem item=new TbItem();
				item.setTitle(goods.getGoods().getGoodsName());
				item.setPrice(goods.getGoods().getPrice());
				item.setStockCount(99999);
				item.setStatus("1");
				item.setIsDefault("1");
				item.setSellerId(goods.getGoods().getSellerId());
				item.setGoodsId(goods.getGoods().getId());

				itemMapper.insert(item);
			}

		}

	}


	/**
	 * 根据规格查询 ITem表
	 * @param map
	 * @param goodsId SPU商品ID
	 * @return
	 */
	public TbItem getItemBySpecMap(Map<String,String> map , Long goodsId){

		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();

		//设置条件1 商品ID
		criteria.andGoodsIdEqualTo(goodsId);

		//设置条件2 商品的标题
		for(String key : map.keySet()){
			criteria.andTitleEqualTo("%"+ map.get(key) +"%");
		}

		List<TbItem> itemList = itemMapper.selectByExample(example);

		//返回数据
		if(itemList.size() > 0){
			return itemList.get(0);
		}else{
			return null;
		}
	}



	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){

		//查商品
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);

		//查询商品扩展信息
		TbGoodsDesc tbGoodsDesc = descMapper.selectByPrimaryKey(id);
		Goods goods = new Goods();
		goods.setGoods(tbGoods);
		goods.setGoodsDesc(tbGoodsDesc);

		//查询商品的明细
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		//设置条件,查询该SPU下所有的SKU
		criteria.andGoodsIdEqualTo(id);

		List<TbItem> itemList = itemMapper.selectByExample(example);
		goods.setItemList(itemList);

		return goods;
	}


	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();

		if(goods!=null){
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
				//精确匹配
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}

		}

		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 开始审核方法(审核通过 or 不通过)
	 * @param ids
	 * @param status
	 */
	@Override
	public void updateStatus(Long[] ids, String status) {

		try{
			//根据ID修改商品审核状态
			for(Long id : ids){
				TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
				tbGoods.setAuditStatus(status);
				goodsMapper.updateByPrimaryKey(tbGoods);

				//判断如果商品审核是 通过("1")
				if("1".equals(status)){
					jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(String.valueOf(id));
						}
					});
				}
			}

		}catch (Exception e){
			e.printStackTrace();
		}
		System.out.println("商品审核通过,发送MQ消息: SPU_ID ="+JSON.toJSONString(ids));

	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {

		//创建SPU对象 设置删除状态为 "1"
		TbGoods tbGoods = new TbGoods();
		tbGoods.setIsDelete("1");

		for(Long id:ids){

			//根据ID 修改为删除状态
			tbGoods.setId(id);
			goodsMapper.updateByPrimaryKeySelective(tbGoods);

			//发送消息,消息内容为SPU商品ID
			jmsTemplate.send("queueSolrDeleteDestination", new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createTextMessage(String.valueOf(id));
				}
			});

		}

		//发送商品的ID
		System.out.println("删除商品,发送MQ消息: SPU_ID = "+JSON.toJSONString(ids));
	}



}
