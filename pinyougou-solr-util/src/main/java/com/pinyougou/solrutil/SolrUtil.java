package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.server.support.SolrServerUtils;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestExecutionListeners;

import java.util.List;
import java.util.Map;

/**
 * @author congzi
 * @Description:  solr工具类-导入商品数据到索引库
 * @create 2018-05-22
 * @Version 1.0
 */

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    //导入数据方法
    public void importData(){
        //查询数据

        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> list = itemMapper.selectByExample(example);

        for(TbItem item:list){

            //将JSON数据转换成MAP ,作为动态域数据保存到索引库
            String spec = item.getSpec();
            Map specMap = JSON.parseObject(spec);
            item.setSpecMap(specMap);
        }
        System.out.println("开始导入数据....");


        solrTemplate.saveBeans(list);
        solrTemplate.commit();
        System.out.println("导入数据完成....");
    }


    public static void main(String[] args) {

      try {
          ApplicationContext content=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
          SolrUtil solrUtil=  (SolrUtil) content.getBean("solrUtil");
          solrUtil.importData();

      }catch (Exception e){
          e.printStackTrace();
      }

    }

    public  void testDeleteAll(){
        Query query=new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

}
