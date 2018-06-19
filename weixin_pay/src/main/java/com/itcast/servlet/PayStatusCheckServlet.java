package com.itcast.servlet;

import com.alibaba.fastjson.JSON;
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
public class PayStatusCheckServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(PayStatusCheckServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //获取要查询的订单号
        String out_trade_no = request.getParameter("out_trade_no");

        //轮询访问微信查询接口 间隔3秒 收到响应之后跳出循环
        while(true){
            Map resultMap = queryOrder(out_trade_no);
            if(resultMap == null){

                log.info("扫码付款失败!!");

                Map<String,String> map = new HashMap();
                map.put("trade_state","ERROR");
                String jsonString = JSON.toJSONString(map);
                response.getWriter().print(jsonString);

                break;
            }

            if(resultMap.get("trade_state").equals("SUCCESS")){
                log.info("扫码支付成功!!");
                String jsonString = JSON.toJSONString(resultMap);
                response.getWriter().print(jsonString);
                break;
            }

            try{
                Thread.sleep(3000);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    /**
     * 调用查询订单接口 查询订单状态
     */
    private Map queryOrder(String out_trade_no) {

        Map<String, String> param = new HashMap<>();
        param.put("appid", PayConfig.appid);//公众号ID
        param.put("mch_id", PayConfig.partner);//商户号
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机数

        try {

            String xmlParam = WXPayUtil.generateSignedXml(param, PayConfig.partnerkey);
            //设置访问的URL
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");

            client.setHttps(true);
            client.setXmlParam(xmlParam);//设置发送的数据
            client.post();
            String xmlResult = client.getContent();//得到结果

            Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);
            return mapResult;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }





}
