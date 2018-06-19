package com.itcast.servlet;

import com.alibaba.fastjson.JSONArray;
import com.github.wxpay.sdk.WXPayUtil;
import com.itcast.config.PayConfig;
import com.itcast.utils.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author congzi
 * @Description:
 * @create 2018-06-04
 * @Version 1.0
 */
public class NativeServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(NativeServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //向微信支付远端接口(URL) https数据

        //1.创建发送的XML数据
        Map<String, String> prarm = new HashMap<>();
        //公众账号ID
        prarm.put("appid", PayConfig.appid);
        //商户号
        prarm.put("mch_id",PayConfig.partner);
        /**
         *  随机字符串(用于秘钥的盐值加密)
         */
        prarm.put("nonce_str", WXPayUtil.generateNonceStr() );
        //商品信息
        prarm.put("body", "传智播客上机费");
        //订单号(后边查询会用)
        prarm.put("out_trade_no", WXPayUtil.getCurrentTimestamp()+"");
        //金额（以分计算）
        prarm.put("total_fee", "1");
        //终端IP
        prarm.put("spbill_create_ip", "127.0.0.1");
        //回调地址(不起作用，但是必须给)
        prarm.put("notify_url",PayConfig.notifyurl);
        //本地
        prarm.put("trade_type", "NATIVE");

        try{
            //1. 生成XML格式的参数信息
            String xmlParam = WXPayUtil.generateSignedXml(prarm, PayConfig.partnerkey);
            log.info("请求微信统一下单接口,参数为: "+xmlParam);

            //2. 通过httpClient向远端接口发送数据
            String URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            HttpClient client = new HttpClient(URL);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            //发送post请求
            client.post();

            //3. 获取返回结果XML,并解析
            String xmlResult = client.getContent();
            log.info("响应结果:" + xmlResult);

            Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlResult);
            Map<String,String> map = new HashMap<>();

            //验证是否请求成功
            if(resultMap.get("return_code").equals("SUCCESS")){
                //成功,生成二维码
                map.put("code_url",resultMap.get("code_url"));
                map.put("return_code","SUCCESS");

            }else{
                map.put("code_url","");
                map.put("return_code","ERROR");
            }

            //订单号
            map.put("out_trade_no",prarm.get("out_trade_no"));
            String jsonString = JSONArray.toJSONString(map);
            response.getWriter().print(jsonString);

        }catch(Exception e){
            e.printStackTrace();
       }


    }
}
