package com.pinyougou.order.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pojo.TbPayLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderExample.Criteria;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.order.service.OrderService;

import entity.PageResult;
import util.IdWorker;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbOrder> page=   (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private TbOrderItemMapper orderItemMapper;

	@Autowired
	private IdWorker idWorker;


	@Autowired
	private TbPayLogMapper payLogMapper;

	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {
		
		//从redis中获取购物车数据
		List<Cart> cartList = (List<Cart>) JSONArray.parseArray((String) redisTemplate.boundHashOps("cartList").get(order.getUserId()),Cart.class);

		/**
		 *  生成订单支付日志
		 *  	总金额
		 *  	订单集合(支付日志中 有一个字段专门保存该笔支付中包含哪些订单)
		 */

		double totalMoney = 0;
		List<String> orderList = new ArrayList<>();

		//循环购物车列表, 循环向订单表添加数据
		for(Cart cart : cartList){
			TbOrder order1 = new TbOrder();
			//订单编号, 分布式ID
			long id = idWorker.nextId();
			order1.setOrderId(id);

			//支付类型
			order1.setPaymentType(order.getPaymentType());
			//状态
			order1.setStatus("1");
			order1.setCreateTime(new Date());
			order1.setUpdateTime(new Date());
			order1.setUserId(order1.getUserId());
			//地址
			order1.setReceiverAreaName(order1.getReceiverAreaName());
			//电话
			order1.setReceiverMobile(order1.getReceiverMobile());
			//收货人
			order1.setReceiver(order.getReceiver());
			//商家ID
			order1.setSellerId(order1.getSellerId());

			//保存订单主表
			orderMapper.insert(order1);

			/**
			 * 支付订单所需要的 订单号
			 */
			orderList.add(order1.getOrderId()+"");

			double money = 0;
			//修改订单明细表
			for(TbOrderItem orderItem : cart.getOrderItemList()){

				//计算订单中所有商品的总价
				money += orderItem.getTotalFee().doubleValue();

				//订单项表 与订单表 多对一的关系
				orderItem.setOrderId(order1.getOrderId());
				orderItem.setSellerId(order1.getSellerId());

				//修改订单明细
				orderItemMapper.insert(orderItem);
			}

			//金额累加
			totalMoney += money;

			order1.setPayment(new BigDecimal(money));
			//更新订单表的总金额
			orderMapper.updateByPrimaryKey(order1);

		}


		/**
		 * 判断是否是微信支付, 如果是微信支付,生成订单log
		 */
		if("1".equals(order.getPaymentType())){
			//创建支付日志对象
			TbPayLog payLog = new TbPayLog();
			//日志创建时间
			payLog.setCreateTime(new Date());
			//保存订单ID列表
			String  ids = orderList.toString().replace("[","").replace("]","").replace(" ","");
			payLog.setOrderList(ids);

			//支付订单号的生成
			payLog.setOutTradeNo(String.valueOf(idWorker.nextId()));
			//支付方式 : 微信
			payLog.setPayType("1");
			//订单金额 分
			payLog.setTotalFee((long)(totalMoney * 100));

			//支付状态
			payLog.setTradeState("0");

			//用户ID
			payLog.setUserId(order.getUserId());

			payLogMapper.insert(payLog);

			/**
			 * 将生成的订单日志信息保存到缓存
			 * 		生成二维码时,需要使用订单的一些信息
			 * 		用户ID作为key
			 */
			redisTemplate.boundHashOps("payLog").put(order.getUserId(), JSON.toJSONString(payLog));

		}

		//清除购物车中的数据
		redisTemplate.boundHashOps("cartList").delete(order.getUserId());


	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKey(order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id){
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			orderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbOrderExample example=new TbOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(order!=null){			
						if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
				criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(order.getPostFee()!=null && order.getPostFee().length()>0){
				criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(order.getStatus()!=null && order.getStatus().length()>0){
				criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(order.getShippingName()!=null && order.getShippingName().length()>0){
				criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
				criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(order.getUserId()!=null && order.getUserId().length()>0){
				criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
				criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
				criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
				criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
				criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
				criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(order.getReceiver()!=null && order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
				criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(order.getSourceType()!=null && order.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(order.getSellerId()!=null && order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
	
		}
		
		Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
