package com.pinyougou.solrutil;

import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.server.support.SolrServerUtils;
import org.springframework.stereotype.Component;

import java.util.List;

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
            System.out.println(item.getTitle());
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
}
