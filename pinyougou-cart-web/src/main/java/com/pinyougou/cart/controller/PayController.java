package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.ctc.wstx.sw.EncodingXmlWriter;
import com.pinyougou.order.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author congzi
 * @Description: 支付控制层
 * @create 2018-06-19
 * @Version 1.0
 */
@RestController
@RequestMapping(value = "/pay")
public class PayController {

    @Reference
    private PayService payService;

    @RequestMapping("/createNative")
    public Map createNative(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Map map = payService.createNative(userId);

        return map;
    }

    @RequestMapping("/checkPayStatus")
    public  void checkPayStatus(String out_trade_no, HttpServletResponse response){

        /**
         * 检测支付状态
         */
        while(true){
            try{
                Map<String,String> map = payService.queryPayStatus(out_trade_no);
                if(map == null){
                    Map resultMap = new HashMap();
                    response.getWriter().print("ERROR");
                    break;
                }
                //支付成功
                if(map.get("trade_state").equals("SUCCESS")){
                    response.getWriter().print("SUCCESS");
                    /**
                     * 修改订单状态
                     *      支付成功就会返回 订单的交易流水号
                     */

                    payService.updateOrderStatus(out_trade_no, map.get("transaction_id"));
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }





}
