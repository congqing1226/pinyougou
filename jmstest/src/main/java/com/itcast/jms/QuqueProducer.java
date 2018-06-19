package com.itcast.jms;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.management.Query;

/**
 * @author congzi
 * @Description:
 * @create 2018-06-10
 * @Version 1.0
 */
public class QuqueProducer {

    public static void main(String[] args) {


        try {
            ConnectionFactory connectionFactory =
                    new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");

            Connection connection = connectionFactory.createConnection();

            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            //创建队列对象
            Queue testQuere = session.createQueue("testQuere");

            //创建消息的生产者
            MessageProducer producer = session.createProducer(testQuere);

            TextMessage textMessage = session.createTextMessage("欢迎使用消息中间件!");

            producer.send(textMessage);

            producer.close();
            session.close();
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }


    }



}
