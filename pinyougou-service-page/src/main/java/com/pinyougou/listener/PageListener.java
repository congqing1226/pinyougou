package com.pinyougou.listener;

import com.pinyougou.sellergoods.service.StaticPageService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * @author congzi
 * @Description: 生成静态页面监听
 * @create 2018-05-29
 * @Version 1.0
 */
public class PageListener implements MessageListener {

    @Autowired
    private StaticPageService staticPageService;

    @Override
    public void onMessage(Message message) {
        //转换消息类型
        ActiveMQTextMessage textMessage = (ActiveMQTextMessage) message;

        try{
            //获取商品ID
            String id = textMessage.getText();
            System.out.println("接收MQ消息,根据商品ID,生成静态化页面!!");
            //重新生成静态页面
            staticPageService.gen_item(Long.parseLong(id));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
