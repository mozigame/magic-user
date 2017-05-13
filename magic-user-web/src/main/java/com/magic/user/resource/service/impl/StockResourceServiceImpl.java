package com.magic.user.resource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.tools.DateUtil;
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
import com.magic.user.exception.UserException;
import com.magic.user.service.AccountIdMappingService;
import com.magic.user.service.LoginService;
import com.magic.user.service.UserService;
import com.magic.user.resource.service.StockResourceService;
import com.magic.user.vo.StockInfoVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public String findAllStock(RequestContext rc) {
        List<StockInfoVo> list = userService.findAllStock();
        assembleStockList(list);
        for (StockInfoVo info : list) {
            info.setShowStatus(AccountStatus.parse(info.getStatus()).desc());
            info.setCurrencyName(CurrencyType.parse(info.getCurrencyType()).desc());
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("list", list);
        return jsonObject.toJSONString();
    }

    @Override
    public String simpleList(RequestContext rc) {
        User opera=userService.get(rc.getUid());
        if (opera==null)
            throw UserException.ILLEGAL_USER;
        List<StockInfoVo> list = userService.findAllStock();
        list = assembleSimple(list);
        JSONObject result = new JSONObject();
        result.put("list", list);
        return result.toJSONString();
    }


    @Override
    public String getStockDetail(RequestContext rc, Long id) {
        User user = userService.get(rc.getUid());
        if (user == null)
            throw UserException.ILLEGAL_USER;
        StockInfoVo stockDetail = userService.getStockDetail(id);
        assembleStockDetail(stockDetail);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("baseInfo", stockDetail);
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

    /**
     * @param list
     * @Doc 组装股东列表展示信息
     */
    private void assembleStockList(List<StockInfoVo> list) {
        for (StockInfoVo info : list) {
            info.setShowStatus(AccountStatus.parse(info.getStatus()).desc());
            info.setRegisterTime(DateUtil.formatDateTime(new Date(Long.parseLong(info.getRegisterTime())), DateUtil.formatDefaultTimestamp));
        }
    }

    private List<StockInfoVo> assembleSimple(List<StockInfoVo> list) {
        List<StockInfoVo> infos = new ArrayList<>();
        for (StockInfoVo info : list) {
            StockInfoVo info1 = new StockInfoVo();
            info1.setId(info.getId());
            info1.setAccount(info.getAccount());
            infos.add(info1);
        }
        return infos;
    }

    /**
     * @param info
     * @Doc 组装股东详情
     */
    private void assembleStockDetail(StockInfoVo info) {
        if (info != null) {
            info.setShowStatus(AccountStatus.parse(info.getStatus()).desc());
            info.setRegisterTime(DateUtil.formatDateTime(new Date(Long.parseLong(info.getRegisterTime())), DateUtil.formatDefaultTimestamp));
            info.setRegisterIp(IPUtil.intToIp(Integer.parseInt(info.getRegisterIp())));
            info.setLastLoginIp(IPUtil.intToIp(Integer.parseInt(info.getLastLoginIp())));
        }
    }

    @Override
    public String updatePwd(RequestContext rc, Long id, String pwd) {
        User opera = userService.get(rc.getUid());
        if (opera == null)
            throw UserException.ILLEGAL_USER;
        User stock = userService.get(id);
        if (stock == null)
            throw UserException.ILLEGAL_USER;
        if (!loginService.resetPassword(id, pwd)) {
            ApiLogger.error("update password error,userId:" + id);
            throw UserException.PASSWORD_RESET_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }

    @Override
    public String update(RequestContext rc, Long id, String telephone, String email, String bankCardNo, String bank, Integer status) {
        User opera = userService.get(rc.getUid());
        if (opera == null)
            throw UserException.ILLEGAL_USER;
        User user = assembleUserUpdate(id, telephone, email, bankCardNo, bank, status);
        int count = userService.update(user);
        if (count <= 0) {
            throw UserException.USER_UPDATE_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * @param id
     * @param telephone
     * @param email
     * @param bankCardNo
     * @param bank
     * @param status
     * @return
     * @Doc 组装修改股东的信息
     */
    private User assembleUserUpdate(Long id, String telephone, String email, String bankCardNo, String bank, Integer status) {
        User user = new User();
        user.setUserId(id);
        user.setTelephone(telephone);
        user.setEmail(email);
        user.setBankCardNo(bankCardNo);
        user.setBank(bank);
        user.setStatus(AccountStatus.parse(status));
        return user;
    }

    @Override
    public String add(RequestContext rc, String account, String password, String realname, String telephone,
                      Integer currencyType, String email, Integer sex) {
        User opera = userService.get(rc.getUid());
        if (opera == null)
            throw UserException.ILLEGAL_USER;
        long ownerId = opera.getOwnerId();
        long userId = uuidService.assignUid();
        //1、判断用户名是否已经存在
        if (accountIdMappingService.getUid(ownerId, account) > 0)
            throw UserException.USERNAME_EXIST;

        //2、添加业主与账号id映射
        OwnerAccountUser ownerAccountUser = new OwnerAccountUser(ownerId + UserContants.SPLIT_LINE + account, userId);
        if (accountIdMappingService.add(ownerAccountUser) <= 0) {
            throw UserException.REGISTER_FAIL;
        }
        //3、添加登录信息
        Login login = new Login(userId, account, password);
        if (loginService.add(login) <= 0) {
            throw UserException.REGISTER_FAIL;
        }
        //4、添加股东基础信息
        User stockUser = assembleStock(userId, realname, account, telephone, email, AccountType.stockholder, GeneraType.parse(sex), CurrencyType.parse(currencyType), IPUtil.ipToInt(rc.getIp()), System.currentTimeMillis(), ownerId);
        if (userService.addStock(stockUser) <= 0) {
            throw UserException.REGISTER_FAIL;
        }
        JSONObject result = new JSONObject();
        result.put("id", userId);
        return result.toJSONString();
    }

    /**
     * @param userId
     * @param realname
     * @param username
     * @param telephone
     * @param email
     * @param type
     * @param gender
     * @param currencyType
     * @param registerIp
     * @param registerTime
     * @param ownerId
     * @return
     * @Doc 组装股东基础数据
     */
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
    public String disable(RequestContext rc, Long id, Integer status) {
        User opera = userService.get(rc.getUid());
        if (opera == null)
            throw UserException.ILLEGAL_USER;
        User stockUser = userService.get(id);
        if (stockUser == null)
            throw UserException.ILLEGAL_USER;
        if (stockUser.getStatus().value() == status)
            throw UserException.USER_STATUS_UPDATE_FAIL;
        if (userService.disable(id, status) <= 0) {
            throw UserException.USER_STATUS_UPDATE_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }
}
