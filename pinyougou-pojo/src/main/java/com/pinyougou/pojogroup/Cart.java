package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

/**
 * @author congzi
 * @Description: 购物车entity
 * @create 2018-06-02
 * @Version 1.0
 */
public class Cart implements Serializable {

    /**
     * 商家Id
     */
    private String sellerId;

    /**
     * 商家名称
     */
    private  String sellerName;

    /**
     * 订单项列表
     */
    private List<TbOrderItem> orderItemList;

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
