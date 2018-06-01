package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author congzi
 * @Description:
 * @create 2018-05-26
 * @Version 1.0
 */

@RestController
public class HtmlController {

    @Autowired
    private FreeMarkerConfigurer config;

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;

    @RequestMapping("gen_item")
    public void gen_item(Long goodsId) throws Exception{

        //1.获取配置文件对象
        Configuration configuration = config.getConfiguration();

        //2.获取模板对象
        Template template = configuration.getTemplate("item.ftl");

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
        Writer out = new FileWriter(new File("F:\\PYG_Freemarker\\"+goodsId+".html"));

        //创建HTML
        template.process(goods,out);
        out.close();
    }


}
