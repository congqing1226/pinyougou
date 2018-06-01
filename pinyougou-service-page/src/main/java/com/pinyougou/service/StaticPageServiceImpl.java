package com.pinyougou.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.sellergoods.service.StaticPageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author congzi
 * @Description: 生成静态化页面服务
 * @create 2018-05-29
 * @Version 1.0
 */
public class StaticPageServiceImpl implements StaticPageService {

    /**
     * 1.获取配置文件对象
     */
    private Configuration configuration;

    public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.configuration = freeMarkerConfigurer.getConfiguration();
    }

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;

    @Value("${PAGEDIR}")
    private String pageDir;


    @Override
    public void gen_item(Long goodsId) {
       try{
           System.out.println("开始生成静态化页面: SPUID ="+ goodsId);

           //2.获取模板对象
           freemarker.template.Template template = configuration.getTemplate("item.ftl");

           //3.创建数据模型 (根据SPU的信息)
           Goods goods = goodsService.findOne(goodsId);

           //查询分类的名称
           String category1 = itemCatService.findOne(goods.getGoods().getCategory1Id()).getName();
           String category2 = itemCatService.findOne(goods.getGoods().getCategory2Id()).getName();
           String category3 = itemCatService.findOne(goods.getGoods().getCategory3Id()).getName();

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
