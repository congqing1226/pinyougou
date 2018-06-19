package com.itcast.jms;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @author congzi
 * @Description:
 * @create 2018-06-10
 * @Version 1.0
 */
public class QuqueCustomer {

    public static void main(String[] args) {
        try{
            //1.创建连接工厂
            ConnectionFactory connectionFactory=new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
            //2.获取连接
            Connection connection = connectionFactory.createConnection();
            //3.启动连接
            connection.start();
            //4.获取session  (参数1：是否启动事务,参数2：消息确认模式)
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //5.创建队列对象
            Queue queue = session.createQueue("testQuere");

            MessageConsumer consumer = session.createConsumer(queue);

            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {

                    try {
                        TextMessage textMessage = (TextMessage) message;
                        String text = textMessage.getText();

                        System.out.println("接收到消息为:" + text);

                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

            //8.等待键盘输入
            System.in.read();
            //9.关闭资源
            consumer.close();
            session.close();
            connection.close();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
