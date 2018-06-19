package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.PayService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;
import config.PayConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import util.HttpClient;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author congzi
 * @Description:
 * @create 2018-06-19
 * @Version 1.0
 */
@Service
public class PayServiceImpl implements PayService {

    private  static  final Logger log = LoggerFactory.getLogger(PayServiceImpl.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbPayLogMapper payLogMapper;

    @Autowired
    private TbOrderMapper orderMapper;

    @Override
    public Map createNative(String userId) {

        //根据UserId从redis中取出支付信息

        TbPayLog payLog =
                JSON.parseObject((String) redisTemplate.boundHashOps("payLog").get(userId), new TypeReference<TbPayLog>() {
        });

        //1 发送XML格式的数据
        Map<String,String> prarm = new HashMap<>();
        //公众账号ID
        prarm.put("appid",PayConfig.appid);
        //商户号
        prarm.put("mch_id",PayConfig.partner);
        //随机字符串
        prarm.put("nonce_str", WXPayUtil.generateNonceStr());
        //商品信息
        prarm.put("body", "品优购");//商品信息

        //订单号
        prarm.put("out_trade_no",payLog.getOutTradeNo());
        //金额（分）
        prarm.put("total_fee", payLog.getTotalFee()+"");

        prarm.put("spbill_create_ip", "127.0.0.1");
        //回调地址(不起作用，但是必须给)
        prarm.put("notify_url", PayConfig.notifyurl);
        //本地
        prarm.put("trade_type", "NATIVE");

        try{
            //生成xml格式的请求参数
            String xml = WXPayUtil.generateSignedXml(prarm, PayConfig.partnerkey);
            log.info("请求参数:"+xml);

            //通过httpclient 向微信接口发送数据
            String  url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            HttpClient httpClient = new HttpClient(url);

            //设置https  发送xml格式数据
            httpClient.setHttps(true);
            httpClient.setXmlParam(xml);
            httpClient.post();

            //获取返回参数
            String content = httpClient.getContent();
            log.info("响应结果为:"+content);

            //判断支付是否成功
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            Map<String,String>  map = new HashMap<>();

            if(resultMap.get("return_code").equals("SUCCESS")){
                //如果成功, 保存生成二维码的URL
                map.put("code_url",resultMap.get("code_url"));
                map.put("return_code","SUCCESS");
            }else{
                map.put("code_url", "");
                map.put("return_code", "ERROR");
            }

            map.put("out_trade_no",prarm.get("out_trade_no"));
            map.put("total_fee", payLog.getTotalFee()+"");

            return map;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public Map queryPayStatus(String out_trade_no) {

        Map<String,String> param=new HashMap<String, String>();
        param.put("appid", PayConfig.appid);//公众号ID
        param.put("mch_id", PayConfig.partner);//商户号
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机数

        try {

            String xmlParam = WXPayUtil.generateSignedXml(param, PayConfig.partnerkey);

            HttpClient client=new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setHttps(true);
            client.setXmlParam(xmlParam);//设置发送的数据
            client.post();
            String xmlResult = client.getContent();//得到结果

            System.out.println(xmlResult);
            Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);
            return mapResult;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 修改订单状态
     * @param out_trade_no  订单号
     * @param transaction_id  交易流水号
     */
    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {

        //根据订单号查询 支付日志信息
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        //设置交易流水号
        payLog.setTransactionId(transaction_id);
        //设置交易时间
        payLog.setPayTime(new Date());
        //更改状态 1 交易完成
        payLog.setTradeState("1");
        payLogMapper.updateByPrimaryKey(payLog);

        //得到订单ID列表
        String orderList = payLog.getOrderList();
        String[] ids = orderList.split(",");

        //设置所有订单状态为已z支付
        for(String id : ids){

            TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.valueOf(id));
            //已经支付
            tbOrder.setStatus("2");
            orderMapper.updateByPrimaryKey(tbOrder);
        }
    }


}
