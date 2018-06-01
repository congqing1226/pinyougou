package com.itcast.demo1;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;

/**
 * @author congzi
 * @Description: freemarker DEMO
 * @create 2018-05-26
 * @Version 1.0
 */
public class Demo1 {

    public static void main(String[] args) throws  Exception{

        Configuration configuration = new Configuration(Configuration.getVersion());

        configuration.setDirectoryForTemplateLoading(new File("F:\\PYG_Work\\springfreemarkerDemo\\src\\main\\resources\\"));

        configuration.setDefaultEncoding("UTF-8");

        //获取模板
        Template template = configuration.getTemplate("test.ftl");

        //创建数据模型(POJO 或者 MAP)
        Map map = new HashMap<>();

        map.put("name","张三");
        map.put("message","欢迎来到品优购!!");

        map.put("success",true);

        List goodsList = new ArrayList();
        Map goods1=new HashMap();
        goods1.put("name", "苹果");
        goods1.put("price", 5.8);

        Map goods2=new HashMap();
        goods2.put("name", "香蕉");
        goods2.put("price", 2.5);

        Map goods3=new HashMap();
        goods3.put("name", "橘子");
        goods3.put("price", 3.2);

        goodsList.add(goods1);
        goodsList.add(goods2);
        goodsList.add(goods3);

        map.put("goodsList", goodsList);
        map.put("today",new Date());

        map.put("info","keep real");
        //创建一个write对象,指定生成HTML的路径
        Writer out = new FileWriter("D:\\freemarker.html");

        //生成页面
        template.process(map,out);

        out.close();
    }



}
