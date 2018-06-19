package com.pinyougou.order.service;

import java.util.Map;

/**
 * 支付服务接口
 * @author Administrator
 *
 */
public interface PayService {

	/**
	 * 生成本地的二维码
	 * @param userId
	 * @return
	 */
	public  Map createNative(String userId);

	/**
	 * 查询支付订单的状态
	 * @param out_trade_no
	 * @return
	 */
	public  Map queryPayStatus(String  out_trade_no);

	/**
	 * 修改订单状态
	 * @param out_trade_no
	 */
	public void  updateOrderStatus(String out_trade_no,String transaction_id);

}
