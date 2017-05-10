package com.magic.user.stockholder.resource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.service.java.UuidService;
import com.magic.user.entity.Login;
import com.magic.user.entity.User;
import com.magic.user.enums.AccountStatus;
import com.magic.user.enums.CurrencyType;
import com.magic.user.enums.GeneraType;
import com.magic.user.service.LoginService;
import com.magic.user.service.UserService;
import com.magic.user.stockholder.resource.service.StockResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/3
 * Time: 15:01
 */
@Service("stockResourceService")
public class StockResourceServiceImpl implements StockResourceService {

    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "loginService")
    private LoginService loginService;
    @Resource
    private UuidService uuidService;

    @Override
    public String findAllStock() {
        List<Map<String, Object>> list = userService.findAllStock();
        for (Map<String, Object> map : list) {
            map.put("showStatus", AccountStatus.parse((Integer) map.get("status")).desc());
            map.put("currencyName", AccountStatus.parse((Integer) map.get("currencyType")).desc());
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("list", list);
        return jsonObject.toJSONString();
    }

    @Override
    public String getStockDetail(long id) {
        Map<String, Object> map = userService.getStockDetail(id);
        map.put("showStatus", AccountStatus.parse((Integer) map.get("status")).desc());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("baseInfo", map);
        //TODO go 获取当期运营概况
        String operation = "{\n" +
                "            \"syncTime\":\"2017-04-18 09:29:33\",\n" +
                "            \"info\":{\n" +
                "                \"bets\":129000,\n" +
                "                \"notes\":34560000,\n" +
                "                \"betTotalMoney\":\"80500000\",\n" +
                "                \"betEffMoney\":\"78966789\",\n" +
                "                \"gains\":\"5800000\"\n" +
                "            }\n" +
                "        }";
        jsonObject.put("operation", operation);
        return jsonObject.toJSONString();
    }

    @Override
    public String updatePwd(long id, String pwd) {
        int count = userService.updatePwd(id, pwd);
        if (count <= 0)
            //todo throw
            ApiLogger.error("update password error,userId:" + id);
        return "";
    }

    @Override
    public String update(long id, String telephone, String email, String bankCardNo, int status) {
        User user = new User();
        user.setUserId(id);
        user.setTelephone(telephone);
        user.setEmail(email);
        user.setBankCardNo(bankCardNo);
        user.setStatus(AccountStatus.parse(status));

        int count = userService.update(user);
        if (count <= 0) {
            //todo
        }
        return "";
    }

    @Override
    public String add(String account, String password, String realname, String telephone,
                      int currencyType, String email, int sex) {
        long userId = uuidService.assignUid();
        User user = new User(userId, realname, account, telephone, email, GeneraType.parse(sex), CurrencyType.parse(currencyType), new Date());
        long id = userService.addStock(user);
        if (id <= 1) {
        }
        Login login = new Login(userId, account, password);
        long count = loginService.add(login);
        if (count <= 0) {
            //todo
        }
        JSONObject result = new JSONObject();
        result.put("id", id);
        return result.toJSONString();
    }

    @Override
    public String disable(long id, int status) {
        int count = userService.disable(id, status);
        return "";
    }
}
