package com.magic.user.stockholder.resource.service;

import com.alibaba.fastjson.JSONObject;
import com.magic.user.entity.User;

import java.util.List;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/3
 * Time: 15:00
 */
public interface StockResourceService {

    JSONObject findAllStock();

    JSONObject getStockDetail(long id);

    String updatePwd(String pwd, long id);

    String update(User user);

    String add(User user);

    String disable(long id, int status);
}
