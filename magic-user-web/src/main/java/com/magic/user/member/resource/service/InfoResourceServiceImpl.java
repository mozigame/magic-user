package com.magic.user.member.resource.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
        return userService.getUid(account,type);
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
    public String modifyInfo(RequestContext rc, Long id, Integer type, String realname, String telephone, String email, String bankCardNo, String bank, String bankDeposit, String loginPassword, String paymentPassword) {
        if (!checkParams(id, type, realname, telephone, email, bankCardNo, bank, bankDeposit, loginPassword, paymentPassword)) {
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
        if (accountType == AccountType.member) {//账号
            Member member = memberService.getMemberById(id);
            if (member == null) {
                throw UserException.ILLEGAL_MEMBER;
            }
            newMap.put("ownerId",member.getOwnerId());
            newMap.put("userId", member.getMemberId());
            boolean result = memberService.updateMember(assembleModifyMember(id, realname, telephone, email, bankCardNo, bank, bankDeposit));
            if (result) {
                if (StringUtils.isNotEmpty(realname)) {
                    newMap.put("realname", realname);
                    oldMap.put("realname", member.getRealname());
                }
                if (StringUtils.isNoneEmpty(telephone)) {
                    newMap.put("telephone", telephone);
                    oldMap.put("telephone", member.getTelephone());
                }
                if (StringUtils.isNoneEmpty(email)) {
                    newMap.put("email", email);
                    oldMap.put("email", member.getEmail());
                }
                if (StringUtils.isNoneEmpty(bankCardNo)) {
                    newMap.put("bankCardNo", bankCardNo);
                    oldMap.put("bankCardNo", member.getBankCardNo());
                }
                if (StringUtils.isNoneEmpty(bank)) {
                    newMap.put("bank", bank);
                    oldMap.put("bank", member.getBank());
                }
                if (StringUtils.isNotEmpty(bankDeposit)) {
                    newMap.put("bankDeposit", bankDeposit);
                    oldMap.put("bankDeposit", member.getBankDeposit());
                }
                userMap.put("userId", member.getMemberId());
                userMap.put("username", member.getUsername());
                userMap.put("ownerId", member.getOwnerId());
                userMap.put("ownerName", member.getOwnerUsername());
                userMap.put("type", type);
                userMap.put("operTime", System.currentTimeMillis());
            }
            if (StringUtils.isNotEmpty(loginPassword)) {
                newMap.put("loginPassword", loginPassword);
                thriftOutAssembleService.passwordReset(assembleLoginPwdBody(member.getMemberId(),member.getUsername(),
                        loginPassword,rc.getIp()), "account");
            }
            if(StringUtils.isNotEmpty(paymentPassword)){
                newMap.put("paymentPassword",paymentPassword);
            }

        } else {//代理或股东
            User user = userService.getUserById(id);
            if (user == null) {
                throw UserException.ILLEGAL_USER;
            }
            //用户数据更新
            boolean result = userService.update(assembleModifyUser(id, realname, telephone, email, bankCardNo, bank, bankDeposit));
            if (result) {
                if (StringUtils.isNoneEmpty(realname)) {
                    newMap.put("realname", realname);
                    oldMap.put("realname", user.getRealname());
                }
                if (StringUtils.isNoneEmpty(telephone)) {
                    newMap.put("telephone", telephone);
                    oldMap.put("telephone", user.getTelephone());
                }
                if (StringUtils.isNoneEmpty(email)) {
                    newMap.put("email", email);
                    oldMap.put("email", user.getEmail());
                }
                if (StringUtils.isNoneEmpty(bankCardNo)) {
                    newMap.put("bankCardNo", bankCardNo);
                    oldMap.put("bankCardNo", user.getBankCardNo());
                }
                userMap.put("userId", user.getUserId());
                userMap.put("username", user.getUsername());
                userMap.put("ownerId", user.getOwnerId());
                userMap.put("ownerName", user.getOwnerName());
                userMap.put("type", type);
                userMap.put("operTime", System.currentTimeMillis());
                userMap.put("paymentPassword",paymentPassword);
            }
            if (StringUtils.isNoneEmpty(loginPassword)) {
                String pwd = PasswordCapture.getSaltPwd(loginPassword);
                result = loginService.resetPassword(id,pwd);
                if (result) {
                    newMap.put("loginPassword", pwd);
                }
            }

        }
        if (newMap.size() > 0) {
            JSONObject object = new JSONObject();
            object.put("after", newMap);
            object.put("before", oldMap);
            object.put("operator", operator);
            object.put("user", userMap);
            producer.send(Topic.USER_INFO_MODIFY_SUCCESS, String.valueOf(uid), object.toJSONString());
        }
        return UserContants.EMPTY_STRING;
    }

    private String assembleLoginPwdBody(Long userId,String username,String newPassword,String ip) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId",userId);
        jsonObject.put("username",username);
        jsonObject.put("newPassword",newPassword);

        jsonObject.put("ip",ip);
        jsonObject.put("appId",10);
        jsonObject.put("operatorTime",System.currentTimeMillis());
        return jsonObject.toJSONString();
    }

    /**
     * 组装修改支付密码的body
     * @param memberId
     * @param payPwd
     * @return
     */
    private String assemblePayPwdBody(Long memberId, String payPwd) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("memberId", memberId);
        jsonObject.put("password",payPwd);
        return jsonObject.toJSONString();
    }

    /**
     * @Doc 组装更新的用户数据
     * @param id
     * @param realname
     * @param telephone
     * @param email
     * @param bankCardNo
     * @param bank
     * @param bankDeposit
     * @return
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
     * @doc 组装更新的会员数据
     * @param id
     * @param realname
     * @param telephone
     * @param email
     * @param bankCardNo
     * @param bank
     * @param bankDeposit
     * @return
     */
    private Member assembleModifyMember(Long id, String realname, String telephone, String email, String bankCardNo, String bank, String bankDeposit) {
        Member member = new Member();
        member.setMemberId(id);
        member.setRealname(realname);
        member.setTelephone(telephone);
        member.setEmail(email);
        member.setBankCardNo(bankCardNo);
        member.setBank(bank);
        member.setBankDeposit(bankDeposit);
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
    private boolean checkParams(Long id, Integer type, String realname, String telephone, String email, String bankCardNo, String bank, String bankDeposit, String loginPassword, String paymentPassword) {
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
                && bank.length() == 0 && bankDeposit.length() == 0 && loginPassword.length() == 0 && paymentPassword.length() == 0) {
            return false;
        }
        return true;

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
        List<AccountModifyListVo> modifyListVos = assembleModifyList(list);

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
