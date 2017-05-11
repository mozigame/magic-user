package com.magic.user.stockholder.resource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.tools.IPUtil;
import com.magic.service.java.UuidService;
import com.magic.user.constants.UserContants;
import com.magic.user.entity.Login;
import com.magic.user.entity.OwnerAccountUser;
import com.magic.user.entity.User;
import com.magic.user.enums.AccountStatus;
import com.magic.user.enums.AccountType;
import com.magic.user.enums.CurrencyType;
import com.magic.user.enums.GeneraType;
import com.magic.user.service.AccountIdMappingService;
import com.magic.user.service.LoginService;
import com.magic.user.service.UserService;
import com.magic.user.stockholder.resource.service.StockResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Resource(name = "accountIdMappingService")
    private AccountIdMappingService accountIdMappingService;

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
    public String simpleList() {
        return null;
    }

    @Override
    public String getStockDetail(Long id) {
        Map<String, Object> map = userService.getStockDetail(id);
        map.put("showStatus", AccountStatus.parse((Integer) map.get("status")).desc());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("baseInfo", map);
        //TODO go 获取当期运营概况
        String operation = "{\n" +
                "    \"syncTime\": \"2017-04-18 09:29:33\",\n" +
                "    \"info\": {\n" +
                "        \"bets\": 129000,\n" +
                "        \"notes\": 34560000,\n" +
                "        \"betTotalMoney\": \"80500000\",\n" +
                "        \"betEffMoney\": \"78966789\",\n" +
                "        \"gains\": \"5800000\"\n" +
                "    }\n" +
                "}";
        jsonObject.put("operation", JSONObject.parseObject(operation));
        return jsonObject.toJSONString();
    }

    @Override
    public String updatePwd(Long id, String pwd) {
        boolean flag = loginService.resetPassword(id, pwd);
        if (!flag)
            //todo throw
            ApiLogger.error("update password error,userId:" + id);
        return UserContants.EMPTY_STRING;
    }

    @Override
    public String update(Long id, String telephone, String email, String bankCardNo, String bank, Integer status) {
        User user = new User();
        user.setUserId(id);
        user.setTelephone(telephone);
        user.setEmail(email);
        user.setBankCardNo(bankCardNo);
        user.setBank(bank);
        user.setStatus(AccountStatus.parse(status));

        int count = userService.update(user);
        if (count <= 0) {
            //todo
        }
        return UserContants.EMPTY_STRING;
    }

    @Override
    public String add(RequestContext rc, String account, String password, String realname, String telephone,
                      Integer currencyType, String email, Integer sex) {
        //todo 获取业主id
        long ownerId = 10;
        long userId = uuidService.assignUid();
        User user = assembleStock(userId, realname, account, telephone, email, AccountType.stockholder, GeneraType.parse(sex), CurrencyType.parse(currencyType), IPUtil.ipToInt(rc.getIp()), System.currentTimeMillis(), ownerId);
        //1、添加用户
        long id = userService.addStock(user);
        if (id <= 1) {
            //todo
        }
        //2、添加登录信息
        Login login = new Login(userId, account, password);
        long count = loginService.add(login);
        if (count <= 0) {
            //todo
        }
        //3、添加业主与账号id映射
        OwnerAccountUser ownerAccountUser = new OwnerAccountUser(ownerId + UserContants.SPLIT_LINE + account, userId);
        long addMapping = accountIdMappingService.add(ownerAccountUser);
        if (addMapping <= 0) {
            //todo
        }
        JSONObject result = new JSONObject();
        result.put("id", userId);
        return result.toJSONString();
    }

    private User assembleStock(Long userId, String realname, String username, String telephone, String email,
                               AccountType type, GeneraType gender, CurrencyType currencyType, Integer registerIp, Long registerTime, Long ownerId) {
        User user = new User();
        user.setUserId(userId);
        user.setRealname(realname);
        user.setUsername(username);
        user.setTelephone(telephone);
        user.setEmail(email);
        user.setType(type);
        user.setGender(gender);
        user.setCurrencyType(currencyType);
        user.setRegisterIp(registerIp);
        user.setRegisterTime(registerTime);
        user.setOwnerId(ownerId);
        return user;
    }


    @Override
    public String disable(Long id, Integer status) {
        int count = userService.disable(id, status);
        if (count <= 0) {
            //todo 如果失败，逻辑处理
        }
        return UserContants.EMPTY_STRING;
    }
}
