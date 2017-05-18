package com.magic.user.resource.service;

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

    String findAllStock(RequestContext rc);

    String simpleList(RequestContext rc );

    String getStockDetail(RequestContext rc,Long id);

    String updatePwd(RequestContext rc,Long id, String pwd);

    String update(RequestContext rc, Long id, String telephone, String email, String bankCardNo, String bank, Integer status);

    String add(RequestContext rc, String account, String password, String realname, String telephone, Integer currencyType, String email, Integer sex);

    String updateStatus(RequestContext rc, Long id, Integer status);
}
