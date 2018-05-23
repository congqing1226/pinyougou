package com.test.solr.pojo;

import org.apache.solr.client.solrj.beans.Field;

public class Goods {
	
	@Field
	private String id;
	
	@Field("item_title")
	private String title;
	
	@Field("item_price")
	private double price;
	
	@Field("item_image")
	private String image;
	
	@Field("item_brand")
	private String brand;
	
	@Field("item_category")
	private String category;
	
	@Field("item_seller")
	private String seller;
	
	@Field("item_goodsid")
	private long goodsid;
	
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public long getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(long goodsid) {
		this.goodsid = goodsid;
	}
	
	
	
	

}
