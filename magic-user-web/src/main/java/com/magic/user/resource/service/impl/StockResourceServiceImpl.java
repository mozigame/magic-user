package com.magic.user.resource.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.model.PageBean;
import com.magic.api.commons.model.SimpleListResult;
import com.magic.api.commons.tools.DateUtil;
import com.magic.api.commons.tools.IPUtil;
import com.magic.oceanus.entity.Summary.OwnerCurrentOperation;
import com.magic.oceanus.service.OceanusProviderDubboService;
import com.magic.user.bean.AgentCondition;
import com.magic.user.constants.UserContants;
import com.magic.user.entity.Login;
import com.magic.user.entity.OwnerAccountUser;
import com.magic.user.entity.User;
import com.magic.user.enums.AccountStatus;
import com.magic.user.enums.AccountType;
import com.magic.user.enums.CurrencyType;
import com.magic.user.enums.GeneraType;
import com.magic.user.exception.UserException;
import com.magic.user.po.DownLoadFile;
import com.magic.user.resource.service.StockResourceService;
import com.magic.user.service.AccountIdMappingService;
import com.magic.user.service.LoginService;
import com.magic.user.service.UserService;
import com.magic.user.service.dubbo.DubboOutAssembleServiceImpl;
import com.magic.user.util.ExcelUtil;
import com.magic.user.util.PasswordCapture;
import com.magic.user.vo.StockInfoVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
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
    @Resource(name = "accountIdMappingService")
    private AccountIdMappingService accountIdMappingService;
    @Resource
    private DubboOutAssembleServiceImpl dubboOutAssembleService;
    @Resource
    private OceanusProviderDubboService oceanusProviderDubboService;
    /**
     * @param rc
     * @return
     * @Doc 查询所有股东
     */
    @Override
    public String findAllStock(RequestContext rc) {
        User user = userService.get(rc.getUid());
        if (user == null) {
            throw UserException.ILLEGAL_USER;
        }
        //TODO
        List<StockInfoVo> list = userService.findAllStock(user.getOwnerId());
        assembleStockList(list);
        for (StockInfoVo info : list) {
            info.setShowStatus(AccountStatus.parse(info.getStatus()).desc());
            info.setCurrencyName(CurrencyType.parse(info.getCurrencyType()).desc());
        }
        SimpleListResult simpleListResult = new SimpleListResult();
        simpleListResult.setList(list);
        return JSON.toJSONString(simpleListResult);
    }

    @Override
    public DownLoadFile listExport(RequestContext rc) {
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            throw UserException.ILLEGAL_USER;
        }

        long uid = rc.getUid(); //业主ID、股东或代理ID
        String filename = ExcelUtil.assembleFileName(uid, ExcelUtil.STOCK_LIST);
        DownLoadFile downLoadFile = new DownLoadFile();
        downLoadFile.setFilename(filename);
        List<StockInfoVo> list = userService.findAllStock(operaUser.getOwnerId());
        assembleStockList(list);
        byte[] content = ExcelUtil.stockListExport(list, filename);
        downLoadFile.setContent(content);
        return downLoadFile;
    }

    /**
     * @param rc
     * @return
     * @Doc 获取股东名称列表
     */
    @Override
    public String simpleList(RequestContext rc) {
        User opera = userService.get(rc.getUid());
        if (opera == null) {
            throw UserException.ILLEGAL_USER;
        }
        List<StockInfoVo> list = userService.findAllStock(opera.getOwnerId());
        list = assembleSimple(list);
        SimpleListResult simpleListResult = new SimpleListResult();
        simpleListResult.setList(list);
        return JSON.toJSONString(simpleListResult);
    }

    /**
     * @param rc
     * @param id
     * @return
     * @Doc 获取股东详情
     */
    @Override
    public String getStockDetail(RequestContext rc, Long id) {
        User user = userService.get(rc.getUid());
        if (user == null) {
            throw UserException.ILLEGAL_USER;
        }
        StockInfoVo stockDetail = userService.getStockDetail(id);
        assembleStockDetail(stockDetail);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("baseInfo", stockDetail);
        OwnerCurrentOperation oco = oceanusProviderDubboService.getShareholderOperation(stockDetail.getId());
//        String operation = "{\n" +
//                "    \"syncTime\": \"2017-04-18 09:29:33\",\n" +
//                "    \"info\": {\n" +
//                "        \"bets\": 129000,\n" +
//                "        \"notes\": 34560000,\n" +
//                "        \"betTotalMoney\": \"80500000\",\n" +
//                "        \"betEffMoney\": \"78966789\",\n" +
//                "        \"gains\": \"5800000\"\n" +
//                "    }\n" +
//                "}";
        JSONObject operation = new JSONObject();
        operation.put("syncTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(oco.getSyncTime()));
        operation.put("info",oco);
        jsonObject.put("operation", operation);
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

    /**
     * @param list
     * @return
     * @Doc 组装股东名称列表
     */
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

    /**
     * @param rc
     * @param id
     * @param pwd
     * @return
     * @Doc 股东密码重置
     */
    @Override
    public String updatePwd(RequestContext rc, Long id, String pwd) {
        //todo 对密码长度做验证
        User opera = userService.get(rc.getUid());
        if (opera == null) {
            throw UserException.ILLEGAL_USER;
        }
        User stock = userService.get(id);
        if (stock == null) {
            throw UserException.ILLEGAL_USER;
        }
        if (!loginService.resetPassword(id, PasswordCapture.getSaltPwd(pwd))) {
            ApiLogger.error("update password error,userId:" + id);
            throw UserException.PASSWORD_RESET_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * @param rc
     * @param id
     * @param realname
     * @param telephone
     * @param email
     * @param bankCardNo
     * @param bank
     * @param status
     * @return
     * @Doc 修改股东信息
     */
    @Override
    public String update(RequestContext rc, Long id, String realname, String telephone, String email, String bankCardNo, String bank, Integer status) {
        User opera = userService.get(rc.getUid());
        if (opera == null) {
            throw UserException.ILLEGAL_USER;
        }
        User updateUser = userService.get(id);
        assembleUserUpdate(updateUser, realname, telephone, email, bankCardNo, bank, status);
        if (!userService.update(updateUser)) {
            throw UserException.USER_UPDATE_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * @param telephone
     * @param email
     * @param bankCardNo
     * @param bank
     * @param status
     * @return
     * @Doc 组装修改股东的信息
     */
    private void assembleUserUpdate(User user, String realname, String telephone, String email, String bankCardNo, String bank, Integer status) {
        user.setRealname(realname);
        user.setTelephone(telephone);
        user.setEmail(email);
        user.setBankCardNo(bankCardNo);
        user.setBank(bank);
        user.setStatus(AccountStatus.parse(status));
    }

    /**
     * @param rc
     * @param account
     * @param password
     * @param realname
     * @param telephone
     * @param currencyType
     * @param email
     * @param sex
     * @return
     * @Doc 添加股东
     */
    @Override
    public String add(RequestContext rc, String account, String password, String realname,String bankCardNo, String bankDeposit, String bank,
                      String telephone, Integer currencyType, String email, Integer sex) {

        User opera = userService.get(rc.getUid());
        if (opera == null) {
            throw UserException.ILLEGAL_USER;
        }
        long ownerId = opera.getOwnerId();
        long userId = dubboOutAssembleService.assignUid();
        if (userId <= 0) {
            throw UserException.ILLEGAL_USER;
        }
        //1、判断用户名是否已经存在
        if (accountIdMappingService.getUid(ownerId, account) > 0) {
            throw UserException.USERNAME_EXIST;
        }

        //2、添加业主与账号id映射
        OwnerAccountUser ownerAccountUser = new OwnerAccountUser(ownerId + UserContants.SPLIT_LINE + account, userId);
        if (accountIdMappingService.add(ownerAccountUser) <= 0) {
            throw UserException.REGISTER_FAIL;
        }
        //3、添加登录信息
        Login login = new Login(userId, account, PasswordCapture.getSaltPwd(password));
        if (loginService.add(login) <= 0) {
            throw UserException.REGISTER_FAIL;
        }
        //4、添加股东基础信息
        User stockUser = assembleStock(userId, realname, bankCardNo, bankDeposit, bank, account, telephone, email, AccountType.stockholder, GeneraType.parse(sex), CurrencyType.parse(currencyType), IPUtil.ipToInt(rc.getIp()), System.currentTimeMillis(), ownerId);
        if (!userService.addStock(stockUser)) {
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
    private User assembleStock(Long userId, String realname,String bankCardNo, String bankDeposit, String bank, String username, String telephone, String email,
                               AccountType type, GeneraType gender, CurrencyType currencyType, Integer registerIp, Long registerTime, Long ownerId) {
        User user = new User();
        user.setUserId(userId);
        user.setRealname(realname);
        user.setBankCardNo(bankCardNo);
        user.setBankDeposit(bankDeposit);
        user.setBank(bank);
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

    /**
     * @param rc
     * @param id
     * @param status
     * @return
     * @Doc 设置股东可用状态
     */
    @Override
    public String updateStatus(RequestContext rc, Long id, Integer status) {

        User opera = userService.get(rc.getUid());
        if (opera == null) {
            throw UserException.ILLEGAL_USER;
        }
        User stockUser = userService.get(id);
        if (stockUser == null) {
            throw UserException.ILLEGAL_USER;
        }
        if (stockUser.getStatus().value() == status) {
            throw UserException.USER_STATUS_UPDATE_FAIL;
        }
        stockUser.setStatus(AccountStatus.parse(status));
        if (!userService.disable(stockUser)) {
            throw UserException.USER_STATUS_UPDATE_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }
}
