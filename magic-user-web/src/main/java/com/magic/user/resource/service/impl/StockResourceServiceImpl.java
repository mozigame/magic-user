package com.magic.user.resource.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.model.SimpleListResult;
import com.magic.api.commons.mq.Producer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.api.commons.tools.IPUtil;
import com.magic.api.commons.tools.LocalDateTimeUtil;
import com.magic.api.commons.tools.NumberUtil;
import com.magic.oceanus.entity.Summary.OwnerCurrentOperation;
import com.magic.oceanus.service.OceanusProviderDubboService;
import com.magic.owner.entity.Resources;
import com.magic.user.constants.UserContants;
import com.magic.user.entity.Login;
import com.magic.user.entity.OwnerAccountUser;
import com.magic.user.entity.OwnerStockAgentMember;
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
import com.magic.user.service.OwnerStockAgentService;
import com.magic.user.service.UserService;
import com.magic.user.service.dubbo.DubboOutAssembleServiceImpl;
import com.magic.user.util.AuthConst;
import com.magic.user.util.ExcelUtil;
import com.magic.user.util.PasswordCapture;
import com.magic.user.vo.FundProfile;
import com.magic.user.vo.StockDetailVo;
import com.magic.user.vo.StockFundInfo;
import com.magic.user.vo.StockInfoVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    @Resource
    private Producer producer;
    @Resource
    private OwnerStockAgentService ownerStockAgentService;

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


    public void authOfSearchResources(Long agentId, Long ownerId, StockInfoVo stockDetail) {
        List<Resources> resources = dubboOutAssembleService.getUserRes(agentId, ownerId);
        boolean hasEmail = false, hasPhone = false, hasBankCardNo = false; //初始化未拥有权限
        if (resources.size() > 0) {
            for (Resources temp : resources) {
                if (temp.getEngKey().trim().equals(AuthConst.STOCK_CHECK_PHONE_KEY)) {
                    hasEmail = true;
                }
                if (temp.getEngKey().trim().equals(AuthConst.STOCK_CHECK_EMIAL_KEY)) {
                    hasPhone = true;
                }
                if (temp.getEngKey().trim().equals(AuthConst.STOCK_CHECK_BANKCARDNO_KEY)) {
                    hasBankCardNo = true;
                }
            }
        }
        if (!hasEmail) {
            stockDetail.setEmail("************");
        }
        if (!hasPhone) {
            stockDetail.setTelephone("************");
        }
        if (!hasBankCardNo) {
            stockDetail.setBankCardNo("************");
        }
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
        //权限检查
        authOfSearchResources(id, user.getOwnerId(), stockDetail);

        assembleStockDetail(stockDetail);
        StockDetailVo stockDetailVo = new StockDetailVo();

        OwnerStockAgentMember osam = ownerStockAgentService.countMembersById(stockDetail.getId(), AccountType.stockholder);
        if (osam != null) {
            stockDetail.setMembers(osam.getMemNumber());
        } else {
            stockDetail.setMembers(0);
        }
        stockDetailVo.setBaseInfo(stockDetail);
        OwnerCurrentOperation oco = dubboOutAssembleService.getShareholderOperation(user.getOwnerId());

        FundProfile<StockFundInfo> profile = new FundProfile<>();
        profile.setSyncTime(LocalDateTimeUtil.toAmerica(System.currentTimeMillis()));
        StockFundInfo info = assembleStockFundInfo(oco);
        profile.setInfo(info);
        stockDetailVo.setOperation(profile);
        return JSON.toJSONString(stockDetailVo);
    }

    /**
     * 组装股东数据
     *
     * @param oco
     * @return
     */
    private StockFundInfo assembleStockFundInfo(OwnerCurrentOperation oco) {
        StockFundInfo stockFundInfo = new StockFundInfo();
        int bets = 0;
        int notes = 0;
        String betTotalMoney = "0";
        String betEffMoney = "0";
        String gains = "0";
        if (Optional.ofNullable(oco).filter(betsValue -> betsValue.getBets() != null && betsValue.getBets() > 0).isPresent()) {
            bets = oco.getBets().intValue();
        }
        if (Optional.ofNullable(oco).filter(notesValue -> notesValue.getNotes() != null && notesValue.getNotes() > 0).isPresent()) {
            notes = oco.getNotes().intValue();
        }
        if (Optional.ofNullable(oco).filter(betTotalMoneyValue -> betTotalMoneyValue.getBetTotalMoney() != null && betTotalMoneyValue.getBetTotalMoney() > 0).isPresent()) {
            betTotalMoney = String.valueOf(NumberUtil.fenToYuan(oco.getBetTotalMoney()));
        }
        if (Optional.ofNullable(oco).filter(betEffMoneyValue -> betEffMoneyValue.getBetEffMoney() != null && betEffMoneyValue.getBetEffMoney() > 0).isPresent()) {
            betEffMoney = String.valueOf(NumberUtil.fenToYuan(oco.getBetEffMoney()));
        }
        if (Optional.ofNullable(oco).filter(gainsValue -> gainsValue.getGains() != null).isPresent()) {
            gains = String.valueOf(NumberUtil.fenToYuan(oco.getGains()));
        }
        stockFundInfo.setBets(bets);
        stockFundInfo.setNotes(notes);
        stockFundInfo.setBetTotalMoney(betTotalMoney);
        stockFundInfo.setBetEffMoney(betEffMoney);
        stockFundInfo.setGains(gains);
        return stockFundInfo;
    }

    /**
     * @param list
     * @Doc 组装股东列表展示信息
     */
    private void assembleStockList(List<StockInfoVo> list) {
        for (StockInfoVo info : list) {
            info.setShowStatus(AccountStatus.parse(info.getStatus()).desc());
            info.setRegisterTime(LocalDateTimeUtil.toAmerica(Long.parseLong(info.getRegisterTime())));
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
            info.setRegisterTime(LocalDateTimeUtil.toAmerica(Long.parseLong(info.getRegisterTime())));
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
    public String add(RequestContext rc, String account, String password, String realname, String bankCardNo, String bankDeposit, String bank,
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
        } else {
            try {
                //用户添加成功发送mq消息，添加默认角色
                producer.send(Topic.MAGIC_OWNER_USER_ADD_SUCCESS, stockUser.getUserId() + "", JSON.toJSONString(stockUser));
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    private User assembleStock(Long userId, String realname, String bankCardNo, String bankDeposit, String bank, String username, String telephone, String email,
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
