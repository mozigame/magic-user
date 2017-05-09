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

    String findAllStock();

    String getStockDetail(long id);

    String updatePwd(long id, String pwd);

    String update(long id, String telephone, String email, String bankCardNo, int status);

    String add(String account, String password, String realname, String telephone, int currencyType, String email, int sex);

    String disable(long id, int status);
}
