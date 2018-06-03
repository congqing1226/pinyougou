package com.pinyougou.cart.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;

import entity.Result;
import util.CookieUtil;

@RestController
@RequestMapping("/cart")
public class CartController {

	private final static Logger log = LoggerFactory.getLogger(CartController.class);

	@Reference
	private CartService cartService;
	
	/**
	 * 添加商品到购物车列表
	 * @param itemId skuID
	 * @param num    sku数量
	 * @return
	 */
	@RequestMapping("/addGoodsToCartList")
	public Result addGoodsToCartList(HttpServletRequest request,HttpServletResponse response, Long itemId, Integer num){

		//允许"http://localhost:9100", 能够跨域请求购物车接口
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:9100");
		//该设置允许可以操作Cookie
		response.setHeader("Access-Control-Allow-Credentials", "true");

		//获取当前登录人
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		log.info("当前用户名: " + username);


		try {
			List<Cart> cartList = findCartList(request);
			//添加商品到购物车
			cartList = cartService.addGoodsToCartList(cartList,itemId,num);

			if(username.equals("anonymousUser")){
				//没有登录
				log.info("用户未登录! 将购物车对象,保存到cookie");
				//将购物车转换为JSON存放到cookie
				CookieUtil.setCookie(request, response,"cartList",JSON.toJSONString(cartList));
			}else{
				log.info("将购物车对象存入redis! 用户名:" + username);
				cartService.saveCartListToRedis(username,cartList);
			}
			return new Result(true, "添加购物车成功!!");
		} catch (RuntimeException e) {
			//接收运行时异常
			return new Result(false, e.getMessage() );
		}catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "添加购物车失败!!");
		}


	}

	/**
	 * 查询当前购物车列表
	 * @param request
	 * @return
	 */
	@RequestMapping("/findCartList")
	public  List<Cart> findCartList(HttpServletRequest request){

		//获取当前用户名
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();

		log.info("当登录用户名: "+ userName);

		//判断如果没有登录,直接从cookie中取出购物车列表
		if(userName.equals("anonymousUser")){

			log.info("用户为登录,从cookie中获取购物车列表!!");
			String cartList = CookieUtil.getCookieValue(request, "cartList");
			if(cartList == null || cartList.equals("")){
				cartList = "[]";
			}
			return JSON.parseArray(cartList,Cart.class);

		}else{

			log.info("用户已经登录,根据用户名从redis中获取购物车列表!!");
			return cartService.findCartListFromRedis(userName);
		}
	}
	
	@RequestMapping("/mergeCartList")
	public void mergeCartList(HttpServletRequest request,HttpServletResponse response){
		
		log.info("开始合并购物车!!");

		//1. 从cookie中提取购物车
		String cartListJson =  CookieUtil.getCookieValue(request,"cartList");
		if(cartListJson == null || cartListJson.equals("")){
			cartListJson = "[]";
		}

		//2. 得到购物车列表
		List<Cart> cartList_cookie = JSON.parseArray(cartListJson, Cart.class);

		//3. 从redis中获取
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		List<Cart> cartList_redis = cartService.findCartListFromRedis(userName);

		//4. 进行合并
		cartList_redis.addAll(cartList_cookie);

		//5. 将合并的集合存储大 redis
		cartService.saveCartListToRedis(userName,cartList_redis);

		//6. 清空购物车
		CookieUtil.setCookie(request,response,"cartList","[]");

		try{
			response.sendRedirect("/cart.html");
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
}
