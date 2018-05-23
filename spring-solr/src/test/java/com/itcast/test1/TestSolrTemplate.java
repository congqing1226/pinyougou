package com.itcast.test1;

import com.test.solr.pojo.Goods;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Crotch;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author congzi
 * @Description:
 * @create 2018-05-22
 * @Version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext-solr.xml")
public class TestSolrTemplate {

    @Autowired
    private SolrTemplate template;

    @Test
    public void testAdd(){

        Goods goods=new Goods();
        goods.setId("2");
        goods.setTitle("华为手机");
        goods.setPrice(5002.22);
        goods.setSeller("华为旗舰店");
        goods.setBrand("三星");
        goods.setCategory("手机");

        template.saveBean(goods);
        template.commit();
    }

    @Test
    public void testSelect(){
        Goods byId = template.getById("2", Goods.class);
        System.out.println(byId.getTitle());
    }

    @Test
    public void testdelete(){
        UpdateResponse response = template.deleteById("2");
        template.commit();
        System.out.println(response.getStatus());
    }

    @Test
    public void testAddList(){

        for(int i=0; i<100; i++){

            Goods goods=new Goods();
            goods.setId(i+1+"");
            goods.setTitle("华为手机");
            goods.setPrice(5002.22);
            goods.setSeller("华为旗舰店");
            goods.setBrand("三星");
            goods.setCategory("手机");

            template.saveBean(goods);
        }
        template.commit();
    }

    @Test
    public void testQuery(){

        Criteria criteria = new Criteria("item_brand").is("三星");
        criteria.and("id").is(16);

        Query query = new SimpleQuery("*:*");

      query.setOffset(10);//起始索引
       query.setRows(1);//每页数量
        ScoredPage<Goods> goods1 = template.queryForPage(query, Goods.class);

        List<Goods> content = goods1.getContent();

        for(Goods goods : content){
            System.out.println(goods.getId()+" "+goods.getTitle()+" "+goods.getBrand() );

        }
    }

    @Test
    public  void deleteAll(){

        Query query = new SimpleQuery("*:*");
        template.delete(query);
        template.commit();
    }
}
