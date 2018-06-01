package com.pinyougou.staticpage.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojogroup.Goods;

import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.staticpage.service.StaticPageService;
import freemarker.template.Configuration;
import freemarker.template.Template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author congzi
 * @Description: 生成静态化页面服务
 * @create 2018-05-29
 * @Version 1.0
 */
@Service
public class StaticPageServiceImpl implements StaticPageService {

    private final static Logger log = LoggerFactory.getLogger(StaticPageServiceImpl.class);


    /**
     * 1.获取配置文件对象
     */
    private Configuration configuration;

    public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.configuration = freeMarkerConfigurer.getConfiguration();
    }

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Autowired
    private TbGoodsMapper tbGoodsMapper;

    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;



    @Value("${PAGEDIR}")
    private String pageDir;


    @Override
    public void gen_item(Long goodsId) {
       try{
           log.info("开始生成静态化页面: SPUID ="+ goodsId);

           Goods goods = new Goods();

           //2.获取模板对象
           freemarker.template.Template template = configuration.getTemplate("item.ftl");

           //3.创建数据模型 (根据SPU的信息)
           TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);
           TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);

           //查询SPU
           goods.setGoods(tbGoods);
           //SPU详细信息
           goods.setGoodsDesc(tbGoodsDesc);

           //查询SKU商品的明细
           TbItemExample example = new TbItemExample();
           TbItemExample.Criteria criteria = example.createCriteria();

           //设置条件,查询该SPU下所有的SKU
           criteria.andGoodsIdEqualTo(goodsId);
           List<TbItem> itemList = tbItemMapper.selectByExample(example);

           goods.setItemList(itemList);

           //查询分类的名称
           String category1 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
           String category2 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
           String category3 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();

           //创建MAP 保存分类信息
           Map map = new HashMap();
           map.put("category1",category1);
           map.put("category2",category2);
           map.put("category3",category3);

           //保存分类名称
           goods.setMap(map);


           //创建 write对象
           Writer out = new FileWriter(new File(pageDir+goodsId+".html"));

           //创建HTML
           template.process(goods,out);
           out.close();
       }catch (Exception e){
           e.printStackTrace();
       }
    }



}
