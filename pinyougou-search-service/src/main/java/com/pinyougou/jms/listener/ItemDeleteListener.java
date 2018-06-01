package com.pinyougou.jms.listener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * @author congzi
 * @Description: 删除索引
 * @create 2018-05-29
 * @Version 1.0
 */
public class ItemDeleteListener implements MessageListener {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage textMessage = (ActiveMQTextMessage) message;
        try{
            //获取商品ID
            String spuId = textMessage.getText();

            //删除索引
            Criteria criteria = new Criteria("item_goodsid").is(spuId);
            SolrDataQuery query = new SimpleQuery(criteria);

            solrTemplate.delete(query);
            solrTemplate.commit();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
