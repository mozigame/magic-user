package com.magic.user.member.resource.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.model.PageBean;
import com.magic.api.commons.mq.Producer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.api.commons.tools.LocalDateTimeUtil;
import com.magic.api.commons.utils.StringUtils;
import com.magic.config.thrift.base.EGResp;
import com.magic.tethys.user.api.service.dubbo.TethysUserDubboService;
import com.magic.user.constants.UserContants;
import com.magic.user.entity.AccountOperHistory;
import com.magic.user.entity.Member;
import com.magic.user.entity.User;
import com.magic.user.enums.AccountType;
import com.magic.user.exception.UserException;
import com.magic.user.po.DownLoadFile;
import com.magic.user.resource.service.AgentResourceService;
import com.magic.user.service.*;
import com.magic.user.service.dubbo.DubboOutAssembleServiceImpl;
import com.magic.user.service.thrift.ThriftOutAssembleServiceImpl;
import com.magic.user.util.ExcelUtil;
import com.magic.user.util.PasswordCapture;
import com.magic.user.vo.AccountModifyInfoVo;
import com.magic.user.vo.AccountModifyListVo;
import com.magic.user.vo.MemberConditionVo;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * InfoResourceServiceImpl
 *
 * @author zj
 * @date 2017/5/9
 */
@Service("infoResourceService")
public class InfoResourceServiceImpl {


    /**
     * 最小
     */
    public static final int MIN_BANK_CARD_NO_LEN = 15;
    /**
     * 最da
     */
    public static final int MAX_BANK_CARD_NO_LEN = 19;

    /** MAX_TELEPHONE_SIZE */
    public static final int MAX_TELEPHONE_SIZE = 11;

    @Resource
    private MemberService memberService;

    @Resource
    private UserService userService;

    @Resource
    private AccountIdMappingService accountIdMappingService;

    @Resource
    private LoginService loginService;

    @Resource
    private AccountOperHistoryService accountOperHistoryService;

    @Resource
    private Producer producer;

    @Resource
    private DubboOutAssembleServiceImpl dubboOutAssembleService;

    @Resource
    private ThriftOutAssembleServiceImpl thriftOutAssembleService;
    @Resource
    private MemberResourceServiceImpl memberResourceService;
    @Resource
    private AgentResourceService agentResourceService;
    @Resource
    private TethysUserDubboService tethysUserDubboService;

    /**
     * memberMongo
     */
    @Resource
    private MemberMongoService memberMongoService;

    private static HashSet<AccountType> sets = new HashSet<>();

    static {
        sets.add(AccountType.member);//会员
        sets.add(AccountType.agent);//代理
        sets.add(AccountType.stockholder);//股东
    }

    /**
     * 资料查询
     *
     * @param rc      RequestContext
     * @param type    账号类型
     * @param account
     * @return
     */
    public String infoDetail(RequestContext rc, Integer type, String account) {
        if (!checkParams(type, account)) {
            throw UserException.ILLEGAL_PARAMETERS;
        }
        long uid = rc.getUid();
        User user = userService.getUserById(uid);
        if (user == null) {
            throw UserException.ILLEGAL_USER;
        }
        long ownerId = user.getOwnerId();//业主ID
        long memberId = getMemberId(type, ownerId, account);
        if (memberId <= 0) {
            throw UserException.ILLEGAL_MEMBER;
        }
        AccountModifyInfoVo modifyInfoVo = getModifyInfo(type, memberId);
        return JSON.toJSONString(modifyInfoVo);
    }

    /**
     * 检查用户是否存在
     *
     * @param rc      RequestContext
     * @param type    账号类型
     * @param account
     * @return
     */
    public String searchUserExist(RequestContext rc, Integer type, String account) {
        if (!checkParams(type, account)) {
            throw UserException.ILLEGAL_PARAMETERS;
        }
        long uid = rc.getUid();
        User user = userService.getUserById(uid);
        if (user == null) {
            throw UserException.ILLEGAL_USER;
        }
        long ownerId = user.getOwnerId();//业主ID
        long memberId = getMemberId(type, ownerId, account);
        if (memberId <= 0) {
            throw UserException.ILLEGAL_MEMBER;
        }
        return "{\"memberId\":" + memberId + "}";
    }

    /**
     * 快速检测
     *
     * @param rc      RequestContext
     * @param type    账号类型
     * @param account
     * @return
     */
    public String accountSearch(RequestContext rc, Integer type, String account) {
        if (!checkParams(type, account)) {
            throw UserException.ILLEGAL_PARAMETERS;
        }
        long uid = rc.getUid();
        User user = userService.getUserById(uid);
        if (user == null) {
            throw UserException.ILLEGAL_USER;
        }
        long ownerId = user.getOwnerId();//业主ID
        long memberId = getMemberId(type, ownerId, account);
        if (memberId <= 0) {
            throw UserException.ILLEGAL_MEMBER;
        }
        if (type == AccountType.agent.value()) {
            return agentResourceService.getDetail(rc, memberId);
        } else if (type == AccountType.member.value()) {
            return memberResourceService.memberDetails(rc, memberId);
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * 获取modifyinfo
     *
     * @param type
     * @param memberId
     * @return
     */
    private AccountModifyInfoVo getModifyInfo(Integer type, long memberId) {
        AccountType accountType = AccountType.parse(type);
        if (accountType == AccountType.member) {
            Member member = memberService.getMemberById(memberId);
            if (member == null) {
                throw UserException.ILLEGAL_MEMBER;
            }
            return assembleModifyInfo(member);
        }
        User user = userService.getUserById(memberId);
        if (user == null) {
            throw UserException.ILLEGAL_USER;
        }
        return assembleModifyInfo(user);

    }

    /**
     * 组装修改数据
     *
     * @param user
     * @return
     */
    private AccountModifyInfoVo assembleModifyInfo(User user) {
        AccountModifyInfoVo result = new AccountModifyInfoVo();
        result.setId(user.getUserId());
        result.setAccount(user.getUsername());
        result.setRealname(user.getRealname());
        result.setBankCardNo(user.getBankCardNo());
        result.setBank(user.getBank());
        result.setBankDeposit(user.getBankDeposit());
        result.setTelephone(user.getTelephone());
        result.setEmail(user.getEmail());
        return result;
    }

    /**
     * 组装修改数据
     *
     * @param member
     * @return
     */
    private AccountModifyInfoVo assembleModifyInfo(Member member) {
        AccountModifyInfoVo result = new AccountModifyInfoVo();
        result.setId(member.getMemberId());
        result.setAccount(member.getUsername());
        result.setRealname(member.getRealname());
        result.setBankCardNo(member.getBankCardNo());
        result.setBank(member.getBank());
        result.setBankDeposit(member.getBankDeposit());
        result.setTelephone(member.getTelephone());
        result.setEmail(member.getEmail());
        return result;
    }

    /**
     * 获取会员ID 账号系统
     *
     * @param ownerId
     * @param account
     * @return
     */
    private long getMemberId(int type, long ownerId, String account) {
        AccountType accountType = AccountType.parse(type);
        if (accountType == AccountType.member) {
            return dubboOutAssembleService.getUid(ownerId, account);
        }
        return userService.getUid(account, type);
    }

    /**
     * 检查参数合法性
     *
     * @param type
     * @param account
     * @return
     */
    private boolean checkParams(Integer type, String account) {
        if (type == null || account == null || account.length() <= 0) {
            return false;
        }
        AccountType accountType = AccountType.parse(type);
        if (accountType == null) {
            return false;
        }
        if (!sets.contains(accountType)) {
            return false;
        }
        return true;
    }

    /**
     * 资料修改
     *
     * @param rc              RequestContext
     * @param id              账号ID
     * @param type            账号类型
     * @param realname        姓名
     * @param telephone       手机号
     * @param email           邮箱
     * @param bankCardNo      银行卡号
     * @param bank            银行名称
     * @param bankDeposit     开户行
     * @param loginPassword   登陆密码
     * @param paymentPassword 支付密码
     * @return
     */
    public String modifyInfo(RequestContext rc, Long id, Integer type, String realname, String telephone, String email, String bankCardNo, String bank, String bankDeposit, String loginPassword, String paymentPassword, String bankCode) {
        if (!checkParams(id, type, realname, telephone, email, bankCardNo, bank, bankDeposit, loginPassword, paymentPassword, bankCode)) {
            throw UserException.ILLEGAL_PARAMETERS;
        }
        long uid = rc.getUid();
        User operator = userService.getUserById(uid);//操作人
        if (operator == null) {
            throw UserException.ILLEGAL_USER;
        }
        Map<String, Object> newMap = new HashMap<>();//修改后数据
        Map<String, Object> oldMap = new HashMap<>();//修改前数据
        Map<String, Object> userMap = new HashedMap();  //修改的用户数据
        AccountType accountType = AccountType.parse(type);
        boolean result = false;
        if (accountType == AccountType.member) {//账号
            result = updateMember(rc, id, type, realname, telephone, email, bankCardNo, bank, bankDeposit, loginPassword, paymentPassword, bankCode, newMap, oldMap, userMap);

        } else {//代理或股东
            User user = userService.getUserById(id);
            if (user == null) {
                throw UserException.ILLEGAL_USER;
            }
            userMap.put("userId", user.getUserId());
            userMap.put("username", user.getUsername());
            userMap.put("ownerId", user.getOwnerId());
            userMap.put("ownerName", user.getOwnerName());
            userMap.put("type", type);
            userMap.put("operTime", System.currentTimeMillis());
            userMap.put("paymentPassword", paymentPassword);
            //用户数据更新
            result = userService.update(assembleModifyUser(id, realname, telephone, email, bankCardNo, bank, bankDeposit));
            if (result) {
                if (StringUtils.isNoneEmpty(realname) && !realname.trim().equals(user.getRealname())) {
                    newMap.put("realname", realname);
                    oldMap.put("realname", user.getRealname());
                }
                if (StringUtils.isNoneEmpty(telephone) && !telephone.trim().equals(user.getTelephone())) {
                    newMap.put("telephone", telephone);
                    oldMap.put("telephone", user.getTelephone());
                }
                if (StringUtils.isNoneEmpty(email) && !email.trim().equals(user.getEmail())) {
                    newMap.put("email", email);
                    oldMap.put("email", user.getEmail());
                }
                if (StringUtils.isNoneEmpty(bankCardNo) && !bankCardNo.trim().equals(user.getBankCardNo())) {
                    newMap.put("bankCardNo", bankCardNo);
                    oldMap.put("bankCardNo", user.getBankCardNo());
                }
            }
            if (StringUtils.isNoneEmpty(loginPassword)) {
                String pwd = PasswordCapture.getSaltPwd(loginPassword);
                result = loginService.resetPassword(id, pwd);
                if (result) {
                    newMap.put("loginPassword", pwd);
                    oldMap.put("loginPassword", "******");
                }
            }

        }
        if (result || StringUtils.isNotBlank(paymentPassword)) {
            JSONObject object = new JSONObject();
            object.put("after", newMap);
            object.put("before", oldMap);
            object.put("operator", operator);
            object.put("user", userMap);
            producer.send(Topic.USER_INFO_MODIFY_SUCCESS, String.valueOf(uid), object.toJSONString());
        }
        return UserContants.EMPTY_STRING;
    }

    private boolean updateMember(RequestContext rc, Long id, Integer type, String realname, String telephone, String email, String bankCardNo, String bank, String bankDeposit, String loginPassword, String paymentPassword, String bankCode, Map<String, Object> newMap, Map<String, Object> oldMap, Map<String, Object> userMap) {
        boolean result = false;
        Member member = memberService.getMemberById(id);
        if (member == null) {
            throw UserException.ILLEGAL_MEMBER;
        }
        newMap.put("ownerId", member.getOwnerId());
        newMap.put("userId", member.getMemberId());

        userMap.put("userId", member.getMemberId());
        userMap.put("username", member.getUsername());
        userMap.put("ownerId", member.getOwnerId());
        userMap.put("ownerName", member.getOwnerUsername());
        userMap.put("type", type);
        userMap.put("operTime", System.currentTimeMillis());
        result = updateMemberDB(id, realname, telephone, email, bankCardNo, bank, bankDeposit, bankCode, newMap, oldMap, member);
        result = updateLoginPassword(rc, loginPassword, newMap, oldMap, result, member);
        if (StringUtils.isNotEmpty(paymentPassword)) {
            newMap.put("paymentPassword", paymentPassword);
            oldMap.put("paymentPassword", "******");
        }
        return result;
    }

    /**
     * 更新登陆密码
     *
     * @param rc
     * @param loginPassword
     * @param newMap
     * @param oldMap
     * @param result
     * @param member
     * @return
     */
    private boolean updateLoginPassword(RequestContext rc, String loginPassword, Map<String, Object> newMap, Map<String, Object> oldMap, boolean result, Member member) {
        if (StringUtils.isNotEmpty(loginPassword)) {
            newMap.put("loginPassword", loginPassword);
            oldMap.put("loginPassword", "******");
            thriftOutAssembleService.passwordReset(assembleLoginPwdBody(member.getMemberId(), member.getUsername(),
                    loginPassword, rc.getIp()), "account");
            result = true;
            StringBuilder msg = new StringBuilder();
            //日志里面不要打印密码
            msg.append("updateLoginPassword::success,").append("member = ").append(member.getMemberId())
                    .append(",memberName = ").append(member.getUsername());
            ApiLogger.info(msg.toString());
        }
        return result;
    }

    /**
     * 更新DB，并同步必要信息到mongo
     *
     * @param id
     * @param realname
     * @param telephone
     * @param email
     * @param bankCardNo
     * @param bank
     * @param bankDeposit
     * @param bankCode
     * @param newMap
     * @param oldMap
     * @param oldMember
     * @return
     */
    private boolean updateMemberDB(Long id, String realname, String telephone,
                                   String email, String bankCardNo, String bank,
                                   String bankDeposit, String bankCode,
                                   Map<String, Object> newMap, Map<String, Object> oldMap, Member oldMember) {
        boolean result;
        Member newMemberInfo = assembleModifyMember(id, realname, telephone, email, bankCardNo, bank, bankDeposit, bankCode);
        result = memberService.updateMember(newMemberInfo);
        if (result) {
            if (StringUtils.isNotEmpty(realname) && !realname.trim().equals(oldMember.getRealname())) {
                newMap.put("realname", realname);
                oldMap.put("realname", oldMember.getRealname());
            }
            if (StringUtils.isNoneEmpty(telephone) && !telephone.trim().equals(oldMember.getTelephone())) {
                newMap.put("telephone", telephone);
                oldMap.put("telephone", oldMember.getTelephone());
            }
            if (StringUtils.isNoneEmpty(email) && !email.trim().equals(oldMember.getEmail())) {
                newMap.put("email", email);
                oldMap.put("email", oldMember.getEmail());
            }
            if (StringUtils.isNoneEmpty(bankCardNo) && !bankCardNo.trim().equals(oldMember.getBankCardNo())) {
                newMap.put("bankCardNo", bankCardNo);
                oldMap.put("bankCardNo", oldMember.getBankCardNo());
            }
            if (StringUtils.isNoneEmpty(bank) && !bank.trim().equals(oldMember.getBank())) {
                newMap.put("bank", bank);
                oldMap.put("bank", oldMember.getBank());
            }
            if (StringUtils.isNotEmpty(bankDeposit) && !bankDeposit.trim().equals(oldMember.getBankDeposit())) {
                newMap.put("bankDeposit", bankDeposit);
                oldMap.put("bankDeposit", oldMember.getBankDeposit());
            }
            //更新数据到Mongo
            updateMemberToMongo(oldMember, newMemberInfo);
        }
        return result;
    }

    private void updateMemberToMongo(Member oldMember, Member newMemberInfo) {
        try {
            if (ApiLogger.isDebugEnabled()) {
                ApiLogger.debug("updateMemberToMongo::oldMember = " + (oldMember == null ? null : JSONObject.toJSONString(oldMember)
                        + ", newMemberInfo = " + (newMemberInfo == null ? null : JSONObject.toJSONString(newMemberInfo))));
            }
            Map<String, Object> updateMap = new HashMap<>();
            if (null != oldMember && null != newMemberInfo) {
                //Telephone
                if (oldMember.getTelephone() != null
                        && (!oldMember.getTelephone().equals(newMemberInfo.getTelephone()))
                        ) {
                    updateMap.put(MemberConditionVo.telephoneString,
                            newMemberInfo.getTelephone());
                }
                //bankCardNo
                if (oldMember.getBankCardNo() != null
                        && (!oldMember.getBankCardNo().equals(newMemberInfo.getBankCardNo()))
                        ) {
                    updateMap.put(MemberConditionVo.bankCardNoString,
                            newMemberInfo.getBankCardNo());
                }
            }
            if (MapUtils.isNotEmpty(updateMap)) {
                boolean updateResult = memberMongoService.updateMemberInfo(newMemberInfo.getMemberId(), updateMap);
                if (!updateResult) {
                    StringBuilder msg = new StringBuilder();
                    msg.append("updateMemberToMongo::error")
                            .append(", oldMember = ").append((oldMember == null ? null : oldMember.getMemberId()))
                            .append(", newMember = ").append((newMemberInfo == null ? null : newMemberInfo.getMemberId()))
                            .append(", updateResult = ").append(updateResult);
                    ApiLogger.error(msg.toString());
                }
            }

        } catch (Exception e) {
            StringBuilder msg = new StringBuilder();
            msg.append("updateMemberToMongo::error")
                    .append(",oldMember = ").append((oldMember == null ? null : oldMember.getMemberId()))
                    .append("newMember = ").append((newMemberInfo == null ? null : newMemberInfo.getMemberId()));
            ApiLogger.error(msg.toString(), e);
        }
    }


    private String assembleLoginPwdBody(Long userId, String username, String newPassword, String ip) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userId);
        jsonObject.put("username", username);
        jsonObject.put("newPassword", newPassword);

        jsonObject.put("ip", ip);
        jsonObject.put("appId", 10);
        jsonObject.put("operatorTime", System.currentTimeMillis());
        return jsonObject.toJSONString();
    }

    /**
     * 组装修改支付密码的body
     *
     * @param memberId
     * @param payPwd
     * @return
     */
    private String assemblePayPwdBody(Long memberId, String payPwd) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("memberId", memberId);
        jsonObject.put("password", payPwd);
        return jsonObject.toJSONString();
    }

    /**
     * @param id
     * @param realname
     * @param telephone
     * @param email
     * @param bankCardNo
     * @param bank
     * @param bankDeposit
     * @return
     * @Doc 组装更新的用户数据
     */
    private User assembleModifyUser(Long id, String realname, String telephone, String email, String bankCardNo, String bank, String bankDeposit) {
        User user = new User();
        user.setUserId(id);
        user.setRealname(realname);
        user.setTelephone(telephone);
        user.setEmail(email);
        user.setBankCardNo(bankCardNo);
        user.setBank(bank);
        user.setBankDeposit(bankDeposit);
        return user;
    }

    /**
     * @param id
     * @param realname
     * @param telephone
     * @param email
     * @param bankCardNo
     * @param bank
     * @param bankDeposit
     * @return
     * @doc 组装更新的会员数据
     */
    private Member assembleModifyMember(Long id, String realname, String telephone, String email, String bankCardNo, String bank, String bankDeposit, String bankCode) {
        Member member = new Member();
        member.setMemberId(id);
        member.setRealname(realname);
        member.setTelephone(telephone);
        member.setEmail(email);
        member.setBankCardNo(bankCardNo);
        member.setBank(bank);
        member.setBankDeposit(bankDeposit);
        member.setBankCode(bankCode);
        return member;
    }

    /**
     * 参数检查
     *
     * @param id
     * @param type
     * @param realname
     * @param telephone
     * @param email
     * @param bankCardNo
     * @param bank
     * @param bankDeposit
     * @param loginPassword
     * @param paymentPassword
     * @return
     */
    private boolean checkParams(Long id, Integer type, String realname, String telephone, String email, String bankCardNo, String bank, String bankDeposit, String loginPassword, String paymentPassword, String bankCode) {
        if (id == null || id <= 0) {
            return false;
        }
        if (type == null || type <= 0) {
            return false;
        }
        AccountType accountType = AccountType.parse(type);
        if (accountType == null) {
            return false;
        }
        if (!sets.contains(accountType)) {
            return false;
        }
        if (realname.length() == 0 && telephone.length() == 0 && email.length() == 0 && bankCardNo.length() == 0
                && bank.length() == 0 && bankDeposit.length() == 0 && loginPassword.length() == 0 && paymentPassword.length() == 0 && bankCode.length() == 0) {
            return false;
        }
        checkBankCardNo(bankCardNo);
        return true;

    }


    /**
     * 检测cardNo
     *
     * @param bankCardNo
     */
    private static void checkBankCardNo(String bankCardNo) {
        if (StringUtils.isNotBlank(bankCardNo)){
            int len = bankCardNo.length();
            if (len < MIN_BANK_CARD_NO_LEN || len > MAX_BANK_CARD_NO_LEN) {
                throw UserException.ILLEGAL_PARAMETERS;
            }
        }
    }


    /**
     * 修改记录列表
     *
     * @param rc
     * @param type
     * @param account
     * @param page
     * @param count
     * @return
     */
    public String modifyList(RequestContext rc, Integer type, String account, int page, int count) {
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            throw UserException.ILLEGAL_USER;
        }
        long uid = rc.getUid();
        long total = accountOperHistoryService.getCount(type, account, uid, operaUser.getOwnerId());
        if (total <= 0) {
            return JSON.toJSONString(assemblePageBean(page, count, 0, null));
        }
        List<AccountOperHistory> list = accountOperHistoryService.getList(type, account, uid, operaUser.getOwnerId(), page, count);
        List<AccountModifyListVo> modifyListVos = assembleModifyList(list);
        return JSON.toJSONString(assemblePageBean(page, count, total, modifyListVos));
    }

    /**
     * 组装数据
     *
     * @param list
     * @return
     */
    private List<AccountModifyListVo> assembleModifyList(List<AccountOperHistory> list) {
        List<AccountModifyListVo> result = new ArrayList<>();
        Iterator<AccountOperHistory> iterator = list.iterator();
        while (iterator.hasNext()) {
            AccountOperHistory next = iterator.next();
            if (next != null) {
                AccountModifyListVo accountModifyListVo = new AccountModifyListVo();
                accountModifyListVo.setAccount(next.getUsername());
                accountModifyListVo.setAccountId(next.getUserId());
                AccountType type = next.getType();
                accountModifyListVo.setType(type.value());
                accountModifyListVo.setShowType(AccountType.getDesc(type));
                accountModifyListVo.setOwnerId(next.getOwnerId());
                accountModifyListVo.setOwnerName(next.getOwnerName());
                accountModifyListVo.setBefore(JSONObject.parseObject(next.getBeforeInfo()));
                accountModifyListVo.setAfter(JSONObject.parseObject(next.getAfterInfo()));
                accountModifyListVo.setOperatorId(next.getProcUserId());
                accountModifyListVo.setOperatorName(next.getProcUsername());
                accountModifyListVo.setOperatorTime(LocalDateTimeUtil.toAmerica(next.getCreateTime()));
                result.add(accountModifyListVo);
            }
        }
        return result;
    }

    /**
     * 组装数据
     *
     * @param list
     * @return
     */
    private List<AccountModifyListVo> assembleModifyListToString(List<AccountOperHistory> list) {
        List<AccountModifyListVo> result = new ArrayList<>();
        Iterator<AccountOperHistory> iterator = list.iterator();
        while (iterator.hasNext()) {
            AccountOperHistory next = iterator.next();
            if (next != null) {
                AccountModifyListVo accountModifyListVo = new AccountModifyListVo();
                accountModifyListVo.setAccount(next.getUsername());
                accountModifyListVo.setAccountId(next.getUserId());
                AccountType type = next.getType();
                accountModifyListVo.setType(type.value());
                accountModifyListVo.setShowType(AccountType.getDesc(type));
                accountModifyListVo.setOwnerId(next.getOwnerId());
                accountModifyListVo.setOwnerName(next.getOwnerName());
                if (next.getBeforeInfo() != null && !"".equals(next.getBeforeInfo())) {
                    StringBuilder before = new StringBuilder();
                    JSONObject beforeJson = JSONObject.parseObject(next.getBeforeInfo());
                    if (null != beforeJson.getString("loginPassword") && !"".equals(beforeJson.getString("loginPassword").trim())) {
                        before.append(" 登录密码:" + beforeJson.getString("loginPassword"));
                    }
                    if (null != beforeJson.getString("email") && !"".equals(beforeJson.getString("email").trim())) {
                        before.append(" 电子邮箱:" + beforeJson.getString("email"));
                    }
                    if (null != beforeJson.getString("realname") && !"".equals(beforeJson.getString("realname").trim())) {
                        before.append(" 姓名:" + beforeJson.getString("realname"));
                    }
                    if (null != beforeJson.getString("bankCardNo") && !"".equals(beforeJson.getString("bankCardNo").trim())) {
                        before.append(" 银行卡号:" + beforeJson.getString("bankCardNo"));
                    }
                    if (null != beforeJson.getString("bank") && !"".equals(beforeJson.getString("bank").trim())) {
                        before.append(" 银行名称:" + beforeJson.getString("bank"));
                    }
                    if (null != beforeJson.getString("bankDeposit") && !"".equals(beforeJson.getString("bankDeposit").trim())) {
                        before.append(" 开户行地址:" + beforeJson.getString("bankDeposit"));
                    }
                    if (null != beforeJson.getString("telephone") && !"".equals(beforeJson.getString("telephone").trim())) {
                        before.append(" 手机号码:" + beforeJson.getString("telephone"));
                    }
                    if (null != beforeJson.getString("paymentPassword") && !"".equals(beforeJson.getString("paymentPassword").trim())) {
                        before.append(" 支付密码:" + beforeJson.getString("paymentPassword"));
                    }
                    accountModifyListVo.setBeforeString(before.toString());
                }
                if (null != next.getAfterInfo() && !"".equals(next.getAfterInfo())) {
                    StringBuilder after = new StringBuilder();
                    JSONObject afterJson = JSONObject.parseObject(next.getAfterInfo());
                    if (afterJson.getString("loginPassword") != null && !"".equals(afterJson.getString("loginPassword").trim())) {
                        after.append(" 登录密码:" + afterJson.getString("loginPassword"));
                    }
                    if (afterJson.getString("email") != null && !"".equals(afterJson.getString("email").trim())) {
                        after.append(" 电子邮箱:" + afterJson.getString("email"));
                    }
                    if (afterJson.getString("realname") != null && !"".equals(afterJson.getString("realname").trim())) {
                        after.append(" 姓名:" + afterJson.getString("realname"));
                    }
                    if (afterJson.getString("bankCardNo") != null && !"".equals(afterJson.getString("bankCardNo").trim())) {
                        after.append(" 银行卡号:" + afterJson.getString("bankCardNo"));
                    }
                    if (afterJson.getString("bank") != null && !"".equals(afterJson.getString("bank").trim())) {
                        after.append(" 银行名称:" + afterJson.getString("bank"));
                    }
                    if (afterJson.getString("bankDeposit") != null && !"".equals(afterJson.getString("bankDeposit").trim())) {
                        after.append(" 开户行地址:" + afterJson.getString("bankDeposit"));
                    }
                    if (afterJson.getString("telephone") != null && !"".equals(afterJson.getString("telephone").trim())) {
                        after.append(" 手机号码:" + afterJson.getString("telephone"));
                    }
                    if (afterJson.getString("paymentPassword") != null && !"".equals(afterJson.getString("paymentPassword").trim())) {
                        after.append(" 支付密码:" + afterJson.getString("paymentPassword"));
                    }
                    accountModifyListVo.setAfterString(after.toString());
                }
                accountModifyListVo.setBefore(JSONObject.parseObject(next.getBeforeInfo()));
                accountModifyListVo.setAfter(JSONObject.parseObject(next.getAfterInfo()));
                accountModifyListVo.setOperatorId(next.getProcUserId());
                accountModifyListVo.setOperatorName(next.getProcUsername());
                accountModifyListVo.setOperatorTime(LocalDateTimeUtil.toAmerica(next.getCreateTime()));
                result.add(accountModifyListVo);
            }
        }
        return result;
    }

    /**
     * 组装翻页数据
     *
     * @param page  页码
     * @param count 当页条数
     * @param total 总条数
     * @param list  详细列表数据
     * @return
     */
    private static PageBean<AccountModifyListVo> assemblePageBean(int page, int count, long total, Collection<AccountModifyListVo> list) {
        PageBean<AccountModifyListVo> result = new PageBean<>();
        result.setPage(page);
        result.setCount(count);
        result.setTotal(total);
        result.setList(list);
        return result;
    }

    /**
     * 资料修改记录导出
     *
     * @param rc      RequestContext
     * @param type    账号类型
     * @param account 账号名
     * @return
     */
    public DownLoadFile modifyListExport(RequestContext rc, Integer type, String account) {
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            throw UserException.ILLEGAL_USER;
        }
        long uid = rc.getUid();
        List<AccountOperHistory> list = accountOperHistoryService.getList(type, account, uid, operaUser.getOwnerId(), null, null);
        List<AccountModifyListVo> modifyListVos = assembleModifyListToString(list);

        String filename = assembleFileName(rc.getUid(), ExcelUtil.MODIFY_LIST);
        DownLoadFile downLoadFile = new DownLoadFile();
        downLoadFile.setFilename(filename);
        //TODO 查询表数据，生成excel的zip，并返回excel byte[]
        byte[] content = ExcelUtil.modifyListExport(modifyListVos, filename);
        downLoadFile.setContent(content);
        return downLoadFile;
    }

    /**
     * 组装文件名
     *
     * @param uid
     * @param name
     * @return
     */
    private String assembleFileName(long uid, String name) {
        StringBuilder filename = new StringBuilder();
        filename.append(uid);
        filename.append("-");
        filename.append(name);
        filename.append(System.currentTimeMillis());
        filename.append(".xlsx");
        return filename.toString();
    }
}
