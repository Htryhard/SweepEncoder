package com.xiaopao.sweepencoder.model;

import java.io.Serializable;

/**
 * 订单实体类
 * Created by Huan on 2017/3/27.
 */

public class Order implements Serializable {
    private String orderId;
    private String num;//购买数量
    private String name;//商品名称
    private String price;//价格
    private String orderNumber;//订单号
    private String image;//商品图片

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
}
