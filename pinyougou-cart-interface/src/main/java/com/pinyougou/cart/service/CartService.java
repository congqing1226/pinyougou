package com.pinyougou.cart.service;

import java.util.List;

import com.pinyougou.pojogroup.Cart;

/**
 * 购物车服务接口
 * @author Administrator
 *
 */
public interface CartService {

	/**
	 * 添加商品到购物车
	 * @param cartList 购物车列表
	 * @param itemId   SKU的ID
	 * @param num	   商品数量
	 * @return	完成操作后的购物车列表
	 */
	public List<Cart>  addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

	List<Cart> findCartListFromRedis(String userName);

	void saveCartListToRedis(String username, List<Cart> cartList);
}
