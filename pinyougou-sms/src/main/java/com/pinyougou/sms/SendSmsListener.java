package com.pinyougou.sms;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author congzi
 * @Description:
 * @create 2018-05-31
 * @Version 1.0
 */
@Component
public class SendSmsListener {

    private final static Logger logger = LoggerFactory.getLogger(SendSmsListener.class);


    @Autowired
    private SmsUtils smsUtils;

    @JmsListener(destination = "sms")
    public void sendSms(Map<String,String> map)throws Exception{

       try{
           logger.info("进入短信网关:"+ JSON.toJSONString(map));
           //发送消息
           smsUtils.sendSms(map);
       }catch (Exception e){

            logger.error("短信发送异常:", e);

       }
    }



}
