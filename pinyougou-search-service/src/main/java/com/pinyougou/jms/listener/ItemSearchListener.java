package com.pinyougou.jms.listener;

import com.pinyougou.search.service.ItemSearchService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * @author congzi
 * @Description: 修改商品监听,更新索引库 与 静态页面内容
 * @create 2018-05-29
 * @Version 1.0
 */
public class ItemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    /**
     * 使用MQ 监听商品添加 与 修改, 同步修改索引库
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage textMessage =(ActiveMQTextMessage)message;

        try {
            String spuId = textMessage.getText();
            System.out.println("MQ监听器接收到商品ID:" + spuId);
            //通过ID ,保存对应的商品到索引库
            itemSearchService.insertProductToSolr(Long.parseLong(spuId));

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
