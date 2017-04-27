package com.xiaopao.sweepencoder.web;

import com.xiaopao.sweepencoder.model.Order;
import com.xiaopao.sweepencoder.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

/**
 * 解析商品类
 * Created by Huan on 2017/3/17 0017.
 */

public class AnalysisHelp {

    /**
     * 根据字符串，解析出用户
     *
     * @param json
     * @return
     */
    public static User getUser(String json) throws JSONException {
        User user = new User();
        JSONObject object = new JSONObject(json);
        user.setId(object.getString("id"));
        user.setEmail(object.getString("email"));
        user.setMobile(object.getString("mobile"));
        user.setCreateTime(object.getString("createTime"));
        user.setLoginIp(object.getString("loginIp"));
        user.setSessionId(object.getString("sessionId"));
        user.setUsername(object.getString("username"));
        return user;
    }

    public static Order getOrder(String json) throws JSONException {
        Order order = new Order();
        JSONObject object = new JSONObject(json);
        String orderGoods = object.getString("orderGoods");
        JSONObject orderGoodsObj = new JSONObject(orderGoods);
        order.setOrderId(orderGoodsObj.getString("orderId"));
        order.setName(orderGoodsObj.getString("name"));
        order.setNum(orderGoodsObj.getString("num"));
        order.setPrice(orderGoodsObj.getString("price"));

        JSONObject orderObj = new JSONObject(object.getString("order"));
        order.setOrderNumber(orderObj.getString("orderNumber"));

        JSONObject goodsObj = new JSONObject(object.getString("goods"));
        order.setImage(goodsObj.getString("image"));
        return order;
    }


}
