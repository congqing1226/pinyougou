package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author congzi
 * @Description: 购物车服务实现
 * @create 2018-06-02
 * @Version 1.0
 */

@Service
public class CartServiceImpl  implements CartService{

    @Autowired
    private TbItemMapper itemMapper;


    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {

        if(cartList == null){
            cartList = new ArrayList<>();
        }

        //1. 根据ID查找商品
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if(item == null){
            throw new RuntimeException("商品没有找到!!");
        }
        if(item.getStatus().equals("0")){
            throw new RuntimeException("商品状态无效!!");
        }

        //2. 得到商家ID
        String sellerId = item.getSellerId();

        //3. 判断购物车列表中是否存在该商家的 购物车
        Cart cart = searchCartListBySellerId(cartList,sellerId);

        //如果在购物车列表中不存在 当前商家的购物车
        if(cart == null){

            if(num == null){
                throw  new RuntimeException("数量填写非法!!");
            }

            /**
             * 创建购物车列表
             */
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());

            //创建购物车明细
            List<TbOrderItem> orderItemList  = new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(item,num);

            //将新构建的购物车明细添加到 orderItemList(购物车明细)
            orderItemList.add(orderItem);

            //将购物车明细列表,添加到购物车对象
            cart.setOrderItemList(orderItemList);

            //将购物车对象,添加到购物车列表
            cartList.add(cart);

        }else{
            //如果存在当前商家的购物车
            //1.根据SKUid 查询商家明细信息
            TbOrderItem tbOrderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);

            //如果没有该SKU的明细信息
            if(tbOrderItem == null){
                if(num <= 0){
                    throw  new RuntimeException("数量非法!!");
                }

                //构建新的购物车明细对象
                TbOrderItem orderItem = createOrderItem(item, num);
                cart.getOrderItemList().add(orderItem);

            }else{
                //如果有SKU的明细信息

                tbOrderItem.setNum(tbOrderItem.getNum() +  num);
                tbOrderItem.setTotalFee(new BigDecimal(tbOrderItem.getPrice().doubleValue() * tbOrderItem.getNum()));

                //修改后如果小于0,从购物车明细列表中移除当前 sku明细
                if(tbOrderItem.getNum() <= 0){
                    cart.getOrderItemList().remove(tbOrderItem);
                }

                //接着判断当前的明细列表 是否已经为空,如果为空就从cartList中移除
                if(cart.getOrderItemList().size() == 0){
                    cartList.remove(cart);
                }
            }
        }

        return cartList;
    }


    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 从redis中获取购物车列表
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String userName) {

        List<Cart> cartList = (List<Cart>)JSONArray.parse((String) redisTemplate.boundHashOps("cartList").get(userName));
        if(cartList == null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    /**
     * 向redis中保存购物车列表
     * @param username
     * @param cartList
     */
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {

        redisTemplate.boundHashOps("cartList").put(username, JSON.toJSONString(cartList));
    }



    /**
     * 在购物车的了列表中,根据商家ID查询购物车对象
     * @param cartList  购物车列表
     * @param sellerId  商家ID
     * @return
     */
    private Cart searchCartListBySellerId(List<Cart> cartList, String sellerId) {

        for(Cart cart : cartList){
            if(cart.getSellerId().equals(sellerId)){
                return cart ;
            }
        }

        return null;
    }

    /**
     * 根据SKU 构建新的购物车明细对象
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {

        TbOrderItem tbOrderItem = new TbOrderItem();
        tbOrderItem.setItemId(item.getId());
        tbOrderItem.setGoodsId(item.getGoodsId());
        tbOrderItem.setSellerId(item.getSellerId());
        tbOrderItem.setTitle(item.getTitle());
        tbOrderItem.setNum(num);
        tbOrderItem.setPrice(item.getPrice());
        //金额
        tbOrderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        tbOrderItem.setPicPath(item.getImage());

        return tbOrderItem;
    }

    /**
     * 根据itemID 查询购物车明细
     * @param orderItemList 购物车明细列表
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {

        /**
         * 遍历查询明细列表中是否已经存在 该SKU商品
         */
        for(TbOrderItem tbOrderItem : orderItemList){
            /**
             *    longValue() 将包装类中的数据拆箱成基本数据类型
             */
            if(tbOrderItem.getItemId().longValue() == itemId.longValue()){
                return tbOrderItem;
            }
        }
        return null;
    }





}
