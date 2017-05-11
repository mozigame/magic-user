package com.magic.user.stockholder.resource.service;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.core.context.RequestContext;
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

    String simpleList();

    String getStockDetail(Long id);

    String updatePwd(Long id, String pwd);

    String update(Long id, String telephone, String email, String bankCardNo, String bank, Integer status);

    String add(RequestContext rc, String account, String password, String realname, String telephone, Integer currencyType, String email, Integer sex);

    String disable(Long id, Integer status);
}
