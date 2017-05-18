package com.magic.user.member.resource.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.model.PageBean;
import com.magic.api.commons.model.SimpleListResult;
import com.magic.api.commons.mq.Producer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.api.commons.tools.CommonDateParseUtil;
import com.magic.api.commons.tools.DateUtil;
import com.magic.api.commons.tools.IPUtil;
import com.magic.api.commons.utils.StringUtils;
import com.magic.commons.enginegw.service.ThriftFactory;
import com.magic.config.service.DomainDubboService;
import com.magic.config.thrift.base.CmdType;
import com.magic.config.thrift.base.EGHeader;
import com.magic.config.thrift.base.EGReq;
import com.magic.config.thrift.base.EGResp;
import com.magic.config.vo.OwnerInfo;
import com.magic.passport.po.SubAccount;
import com.magic.passport.service.dubbo.PassportDubboService;
import com.magic.service.java.UuidService;
import com.magic.user.bean.Account;
import com.magic.user.bean.MemberCondition;
import com.magic.user.bean.RegionNumber;
import com.magic.user.bean.Register;
import com.magic.user.constants.UserContants;
import com.magic.user.entity.Member;
import com.magic.user.entity.OnlineMemberConditon;
import com.magic.user.entity.User;
import com.magic.user.enums.AccountStatus;
import com.magic.user.enums.AccountType;
import com.magic.user.enums.CurrencyType;
import com.magic.user.enums.LoginType;
import com.magic.user.exception.UserException;
import com.magic.user.po.DownLoadFile;
import com.magic.user.po.OnLineMember;
import com.magic.user.po.RegisterReq;
import com.magic.user.service.AccountIdMappingService;
import com.magic.user.service.MemberMongoService;
import com.magic.user.service.MemberService;
import com.magic.user.service.UserService;
import com.magic.user.vo.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * MemberResourceServiceImpl
 *
 * @author zj
 * @date 2017/5/6
 */
@Service("memberServiceResource")
public class MemberResourceServiceImpl {

    @Resource
    private MemberService memberService;

    @Resource
    private UserService userService;

    @Resource
    private AccountIdMappingService accountIdMappingService;

    @Resource
    private MemberMongoService memberMongoService;

    @Resource
    private Producer producer;
    @Resource
    private UuidService uuidService;
    @Resource
    private PassportDubboService passportDubboService;
    @Resource
    private ThriftFactory thriftFactory;
    @Resource
    private DomainDubboService domainDubboService;


    private static final String MEMBER_UPDATE_SUCCESS="MEMBER_UPDATE_SUCCESS";

    /**
     * 会员列表
     *
     * @param rc
     * @param condition
     * @param page
     * @param count
     * @return
     */
    public String memberList(RequestContext rc, String condition, int page, int count) {
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            throw UserException.ILLEGAL_USER;
        }
        MemberCondition memberCondition = MemberCondition.valueOf(condition);
        if (!checkCondition(memberCondition)) {
            return JSON.toJSONString(assemblePageBean(page, count, 0, null));
        }
        long total = memberMongoService.getCount(memberCondition);
        if (total <= 0) {
            return JSON.toJSONString(assemblePageBean(page, count, 0, null));
        }
        //获取mongo中查询到的会员列表
        memberCondition.setOwnerId(operaUser.getOwnerId());
        List<MemberConditionVo> memberConditionVos = memberMongoService.queryByPage(memberCondition, page, count);
        //todo 组装mongo中拿取的列表
        List<?> list = new ArrayList<>();
        List<MemberListVo> members = assembleMemberVos(list);
        return JSON.toJSONString(assemblePageBean(page, count, total, members));
    }

    /**
     * 组装会员列表
     *
     * @param list
     * @return
     */
    private List<MemberListVo> assembleMemberVos(List<?> list) {

        //TODO list包含 会员ID,会员账号，代理ID,代理账号，状态，层级ID，最近登陆时间

        //TODO 1.根据会员ID查询余额balance @sundy dubbo

        //TODO 2.根据会员ID查询返水方案ID,名称,层级名称  @andy dubbo

        //TODO foreach list for item
        return null;
    }

    /**
     * 检查请求参数
     *
     * @param condition
     * @return
     */
    private boolean checkCondition(MemberCondition condition) {
        Integer currencyType = condition.getCurrencyType();
        if (currencyType != null && CurrencyType.parse(currencyType) == null) {
            return false;
        }
        Account account = condition.getAccount();
        if (account != null && StringUtils.isNoneEmpty(account.getName())) {
            Integer type = account.getType();
            if (type == null || AccountType.parse(type) == null) {
                return false;
            }
        }
        RegionNumber region = condition.getDepositNumber();
        if (region != null && region.getMin() != null && region.getMax() != null && region.getMin() > region.getMax()) {
            return false;
        }
        Register register = condition.getRegister();
        if (register != null && register.getStart() != null && register.getEnd() != null && register.getStart() > register.getEnd()) {
            return false;
        }
        region = condition.getWithdrawNumber();
        if (region != null && region.getMin() != null && region.getMax() != null && region.getMin() > region.getMax()) {
            return false;
        }
        Integer status = condition.getStatus();
        if (status != null && AccountStatus.parse(status) == null) {
            return false;
        }
        return true;
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
    private static PageBean<MemberListVo> assemblePageBean(int page, int count, long total, Collection<MemberListVo> list) {
        PageBean<MemberListVo> result = new PageBean<>();
        result.setPage(page);
        result.setCount(count);
        result.setTotal(total);
        result.setList(list);
        return result;
    }

    /**
     * 会员列表导出
     *
     * @param rc        RequestContext
     * @param condition 检索条件
     * @return
     */
    public DownLoadFile memberListExport(RequestContext rc, String condition) {
        MemberCondition memberCondition = MemberCondition.valueOf(condition);
        if (!checkCondition(memberCondition)) {
            throw UserException.ILLEGAL_PARAMETERS;
        }
        long uid = rc.getUid(); //业主ID、股东或代理ID
        String filename = assembleFileName(uid, "会员列表");
        DownLoadFile downLoadFile = new DownLoadFile();
        downLoadFile.setFilename(filename);
        //TODO 查询表数据，生成excel的zip，并返回zip byte[]
        byte[] content = new byte[5];
        downLoadFile.setContent(content);
        return downLoadFile;
    }

    /**
     * 会员详情
     *
     * @param rc RequestContext
     * @param id 会员ID
     * @return
     */
    public String memberDetails(RequestContext rc, long id) {
        Member member = memberService.getMemberById(id);
        SubAccount subAccount = passportDubboService.getSubLoginById(member.getMemberId());

        if (member == null) {
            throw UserException.ILLEGAL_MEMBER;
        }
        //TODO 1.jason 根据会员ID查询会员优惠方案
        //TODO 2.jason 根据会员ID查询会员资金概括
        //TODO 3.jason 根据会员ID查询投注记录
        //TODO 4.jason 根据会员ID查询优惠记录
        MemberDetailVo detail = assembleMemberDetail(member);
        return JSON.toJSONString(detail);
    }

    /**
     * 组装会员详情 //TODO
     *
     * @return
     */
    private MemberDetailVo assembleMemberDetail(Member member) {
        MemberDetailVo vo = new MemberDetailVo();
        ////会员基础信息
        vo.setBaseInfo(assembleMemberInfo(member));
        //todo 会员优惠、资金概况等信息去 jason 那边拿取
        //会员优惠方案
        String preferScheme = "{\n" +
                "    \"level\": 1,\n" +
                "    \"showLevel\": \"未分层\",\n" +
                "    \"onlineDiscount\": \"100返10\",\n" +
                "    \"depositFee\": \"无\",\n" +
                "    \"withdrawFee\": \"无\",\n" +
                "    \"returnWater\": \"返水基本1\",\n" +
                "    \"depositDiscountScheme\": \"100返10\"\n" +
                "}";
        MemberPreferScheme memberPreferScheme = JSONObject.parseObject(preferScheme, MemberPreferScheme.class);
        vo.setPreferScheme(memberPreferScheme);
        ///会员资金概况
        String memberFundInfo = "{\n" +
                "    \"balance\": \"1805.50\",\n" +
                "    \"depositNumbers\": 15,\n" +
                "    \"depositTotalMoney\": \"29006590\",\n" +
                "    \"lastDeposit\": \"1200\",\n" +
                "    \"withdrawNumbers\": 10,\n" +
                "    \"withdrawTotalMoney\": \"24500120\",\n" +
                "    \"lastWithdraw\": \"2500\"\n" +
                "}";

        MemberFundInfo memberFundInfoObj = JSONObject.parseObject(memberFundInfo, MemberFundInfo.class);
        FundProfile fundProfile = new FundProfile();
        fundProfile.setInfo(memberFundInfoObj);
        fundProfile.setSyncTime(DateUtil.formatDateTime(new Date(), DateUtil.formatDefaultTimestamp));
        vo.setFundProfile(fundProfile);
        //投注记录
        String memberBetHistory = "{\n" +
                "    \"totalMoney\": \"29000\",\n" +
                "    \"effMoney\": \"28000\",\n" +
                "    \"gains\": \"18000\"\n" +
                "}";
        MemberBetHistory memberBetHistoryObj = JSONObject.parseObject(memberBetHistory, MemberBetHistory.class);
        vo.setBetHistory(memberBetHistoryObj);
        //优惠记录
        String memberDiscountHistory = "{\n" +
                "    \"totalMoney\": \"1350\",\n" +
                "    \"numbers\": 98,\n" +
                "    \"returnWaterTotalMoney\": \"1450\"\n" +
                "}";
        MemberDiscountHistory memberDiscountHistoryObj = JSONObject.parseObject(memberDiscountHistory, MemberDiscountHistory.class);
        vo.setDiscountHistory(memberDiscountHistoryObj);
        return vo;
    }

    /**
     * @Doc 组装会员基础信息中的会员信息
     * @param member
     * @return
     */
    private MemberInfo assembleMemberInfo(Member member) {
        MemberInfo info = new MemberInfo();
        info.setId(member.getMemberId());
        info.setAccount(member.getUsername());
        info.setAgentId(member.getAgentId());
        info.setAgent(member.getAgentUsername());
        info.setRealname(member.getRealname());
        info.setRegisterTime(DateUtil.formatDateTime(new Date(member.getRegisterTime()), DateUtil.formatDefaultTimestamp));
        info.setRegisterIp(IPUtil.intToIp(member.getRegisterIp()));
        info.setEmail(member.getEmail());
        info.setStatus(member.getStatus().value());
        info.setShowStatus(member.getStatus().desc());
        info.setBankCardNo(member.getBankCardNo());
        SubAccount subAccount = passportDubboService.getSubLoginById(member.getMemberId());
        info.setLastLoginIp(IPUtil.intToIp(subAccount.getLastIp()));
        return info;
    }

    /**
     * 密码重置 -- 后台
     *
     * @param rc
     * @param id
     * @param password
     * @return
     */
    public String passwordReset(RequestContext rc, long id, String password) {
        Member member = memberService.getMemberById(id);
        if (member == null) {
            throw UserException.ILLEGAL_MEMBER;
        }
        //组装请求数据
        String body = assembleReqBody(rc, member, password);
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100006, body);
        EGResp resp = thriftFactory.call(req, "account");
        if (resp != null && resp.getCode() == 0x4444) {//重置成功
            return UserContants.EMPTY_STRING;
        }
        throw UserException.PASSWORD_RESET_FAIL;
    }

    /**
     * 组装请求
     *
     * @param rc
     * @param member
     * @param password
     * @return
     */
    private String assembleReqBody(RequestContext rc, Member member, String password) {
        JSONObject body = new JSONObject();
        body.put("userId", member.getId());
        body.put("username", member.getUsername());
        body.put("newPassword", password);
        body.put("appId", rc.getClient().getAppId());
        body.put("ip", rc.getIp());
        body.put("operatorTime", System.currentTimeMillis());
        return body.toJSONString();
    }

    /**
     * 组装请求对象
     *
     * @param cmdType
     * @param cmd
     * @param body
     * @return
     */
    private EGReq assembleEGReq(CmdType cmdType, int cmd, String body) {
        EGReq req = new EGReq();
        EGHeader header = new EGHeader();
        header.setType(cmdType);
        header.setCmd(cmd);
        req.setHeader(header);
        req.setBody(body);
        return req;
    }

    /**
     * 会员强制下线
     *
     * @param rc
     * @param id
     * @return
     */
    public String logout(RequestContext rc, long id) {
        Member member = memberService.getMemberById(id);
        if (member == null) {
            throw UserException.ILLEGAL_MEMBER;
        }
        //组装请求数据
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100005, logoutBody(rc, member));
        EGResp resp = thriftFactory.call(req, "account");
        if (resp != null && (resp.getCode() == 0x5555 || resp.getCode() == 0x1013)) {//注销成功
            return UserContants.EMPTY_STRING;
        }
        throw UserException.LOGOUT_FAIL;
    }

    /**
     * 强制下线请求body
     *
     * @param rc
     * @param member
     * @return
     */
    private String logoutBody(RequestContext rc, Member member) {
        JSONObject body = new JSONObject();
        body.put("userId", member.getId());
        body.put("username", member.getUsername());
        body.put("deviceId", rc.getClient().getDeviceId());
        body.put("operatorTime", System.currentTimeMillis());
        return body.toJSONString();
    }

    /**
     * 更新会员基础信息
     *
     * @param rc
     * @param id
     * @param realname
     * @param telephone
     * @param email
     * @param bankCardNo
     * @param status
     * @return
     */
    public String update(RequestContext rc, long id, String realname, String telephone, String email, String bankCardNo, int status) {
        Member member = assembleMember(id, realname, telephone, email, bankCardNo, status);
        boolean result = memberService.updateMember(member);
        if (!result) {
            throw UserException.MEMBER_UPDATE_FAIL;
        }
        if (status > 0) {
            sendMemberStatusUpdateMq(id, status);
        }

        return UserContants.EMPTY_STRING;
    }

    /**
     * 组装会员数据
     *
     * @param id
     * @param realname
     * @param telephone
     * @param email
     * @param bankCardNo
     * @param status
     * @return
     */
    private Member assembleMember(long id, String realname, String telephone, String email, String bankCardNo, int status) {
        Member member = new Member();
        member.setMemberId(id);
        member.setRealname(realname);
        member.setTelephone(telephone);
        member.setEmail(email);
        member.setBankCardNo(bankCardNo);
        member.setStatus(AccountStatus.parse(status));
        return member;
    }

    /**
     * 会员层级更新
     *
     * @param rc
     * @param id
     * @param level
     * @return
     */
    @Deprecated
    public String updateLevel(RequestContext rc, long id, int level) {
        Member member = memberService.getMemberById(id);
        if (member == null) {
            throw UserException.ILLEGAL_MEMBER;
        }
        //TODO andy dubbo
        boolean result = false;
        if (!result) {
            throw UserException.MEMBER_LEVEL_UPDATE_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * 会员层级列表
     *
     * @param rc    RequestContext
     * @param lock  是否锁定 1：非锁定 2锁定
     * @param page  页码
     * @param count 每页数据量
     * @return
     */
    public String memberLevelList(RequestContext rc, int lock, int page, int count) {
        SimpleListResult<List<MemberLevelListVo>> result = new SimpleListResult<>();
        //TODO andy dubbo
        List<MemberLevelListVo> list = new ArrayList<>();
        result.setList(list != null ? list : new ArrayList<>());
        return JSON.toJSONString(result);
    }

    /**
     * 会员层级列表导出
     *
     * @param rc   RequestContext
     * @param lock 是否锁定 1：非锁定 2锁定
     * @return
     */
    public DownLoadFile memberLevelListExport(RequestContext rc, int lock) {
        long uid = rc.getUid(); //业主ID、股东或代理ID
        String filename = assembleFileName(uid, "会员层级列表");
        DownLoadFile downLoadFile = new DownLoadFile();
        downLoadFile.setFilename(filename);
        //TODO andy dubbo 查询表数据，生成excel的zip，并返回zip byte[]
        byte[] content = new byte[5];
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

    /**
     * 状态变更
     *
     * @param rc     RequestContext
     * @param id     会员ID
     * @param status 1 启用 2禁用
     * @return
     */
    public String memberStatusUpdate(RequestContext rc, Long id, Integer status) {
        if (!checkParams(id, status)) {
            throw UserException.ILLEGAL_PARAMETERS;
        }
        AccountStatus newStatus = AccountStatus.parse(status);
        AccountStatus oldStatus = AccountStatus.enable;
        if (newStatus == AccountStatus.enable) {
            oldStatus = AccountStatus.disable;
        }
        boolean result = memberService.updateStatus(id, oldStatus, newStatus);
        if (!result) {
            throw UserException.MEMBER_STATUS_UPDATE_FAIL;
        }
        sendMemberStatusUpdateMq(id,status);
        return UserContants.EMPTY_STRING;
    }

    /**
     * @Doc 发送会员状态修改
     * @param id
     * @param status
     */
    private void sendMemberStatusUpdateMq(Long id,Integer status) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("memberId", id);
        jsonObject.put("status", status);
        producer.send(Topic.MEMBER_STATUS_UPDATE_SUCCESS, id + "", jsonObject.toJSONString());
    }

    /**
     * 参数检查
     *
     * @param id
     * @param status
     * @return
     */
    private boolean checkParams(Long id, Integer status) {
        if (id == null || id <= 0) {
            return false;
        }
        if (status == null) {
            return false;
        }
        AccountStatus accountStatus = AccountStatus.parse(status);
        if (accountStatus != AccountStatus.disable || accountStatus != AccountStatus.enable) {
            return false;
        }
        return true;
    }

    /**
     * 会员注册
     *
     * @param rc  RequestContext
     * @param url 注册来源
     * @param req 注册请求数据
     * @return
     */
    public String memberRegister(RequestContext rc, String url, RegisterReq req) {
        if (!checkRegisterParam(req)) {
            throw UserException.ILLEGAL_PARAMETERS;
        }
        OwnerInfo ownerInfo = domainDubboService.getOwnerInfoByDomain(url);
        if (ownerInfo == null || ownerInfo.getOwnerId() < 0) {
            throw UserException.ILLEGAL_SOURCE_URL;
        }
        //todo holderId暂时注释掉
        long holderId = accountIdMappingService.getUid(ownerInfo.getOwnerId(), ownerInfo.getOwnerName());//股东id
        if (holderId <= 0) {
            throw UserException.ILLEGAL_USER;
        }
        User holder = userService.getUserById(holderId);
        if (holder == null) {
            throw UserException.ILLEGAL_USER;
        }
        User agent = null;//所属代理ID
        String proCode = req.getProCode();
        if (StringUtils.isNoneEmpty(proCode)) {
            User agentUser = userService.getUserByCode(proCode);
            if (agentUser != null && agentUser.getOwnerId() == ownerInfo.getOwnerId()) {
                agent = agentUser;
            }
        }
        if (agent == null) {
            long agentId = accountIdMappingService.getUid(ownerInfo.getOwnerId(), ownerInfo.getOwnerName() + "_dl");
            if (agentId > 0) {
                agent = userService.getUserById(agentId);
            }
        }
        if (agent == null) {
            throw UserException.ILLEGAL_USER;
        }
        String body = assembleRegisterBody(rc, url, ownerInfo.getOwnerId(), agent.getId(), req);
        EGReq egReq = assembleEGReq(CmdType.PASSPORT, 0x100001, body);

        //todo 暂时注释调用引擎网关的逻辑
        /*EGResp resp = thriftFactory.call(egReq, "account");
        if (resp == null) {
            throw UserException.REGISTER_FAIL;
        }
        int code = resp.getCode();
        if (code == 0x1004) {
            throw UserException.USERNAME_EXIST;
        }
        if (code != 0x1111) {
            throw UserException.REGISTER_FAIL;
        }*/
        //todo
//        long userId = 0l;
        long userId = uuidService.assignUid();
        /*try {
            userId = Long.parseLong(resp.getData());
        } catch (Exception e) {
            ApiLogger.error(String.format("passport register return data error. resp: %s", JSON.toJSONString(resp)), e);
        }*/
        if (userId <= 0) {
            throw UserException.REGISTER_FAIL;
        }
        Member member = assembleMember(rc, req, userId, ownerInfo.getOwnerId(), ownerInfo.getOwnerName(), holder, agent, url);
        boolean result = memberService.saveMember(member);
        if (!result) {
            throw UserException.REGISTER_FAIL;
        }
        sendRegisterMessage(member);
        return UserContants.EMPTY_STRING;
    }

    /**
     * 发送注册成功消息
     *
     * @param member
     * @return
     */
    private boolean sendRegisterMessage(Member member) {
        try {
            //TODO topic + 消费者
            return producer.send(Topic.MEMBER_REGISTER_SUCCESS, String.valueOf(member.getId()), JSON.toJSONString(member));
        } catch (Exception e) {
            ApiLogger.error(String.format("send member register success mq message error. member: %s", JSON.toJSONString(member)), e);
            return false;
        }
    }

    /**
     * 组装member数据
     *
     * @param rc
     * @param req
     * @param userId
     * @param ownerId
     * @param ownerName
     * @param holder
     * @param agent
     * @return
     */
    private Member assembleMember(RequestContext rc, RegisterReq req, long userId, long ownerId, String ownerName, User holder, User agent, String url) {
        Member member = new Member();
        //todo
        member.setUsername(req.getUsername());
        member.setRealname(req.getRealname());
        member.setTelephone(req.getTelephone());
        member.setEmail(req.getEmail());
        member.setBank(req.getBank());
        member.setBankCardNo(req.getBankCardNo());
        member.setBankDeposit(req.getBankReposit());
        member.setBank(req.getBank());
        member.setMemberId(userId);
        member.setOwnerId(ownerId);
        member.setOwnerUsername(ownerName);
        member.setStockId(holder.getUserId());
        member.setStockUsername(holder.getUsername());
        member.setOwnerId(ownerId);
        member.setOwnerUsername(ownerName);
        member.setAgentId(agent.getUserId());
        member.setAgentUsername(agent.getUsername());
        member.setSourceUrl(url);
        member.setRegisterIp(IPUtil.ipToInt(rc.getIp()));
        member.setRegisterTime(System.currentTimeMillis());
        return member;
    }

    /**
     * 组装passport注册请求数据
     *
     * @param rc
     * @param url
     * @param ownerId
     * @param agentId
     * @param req
     * @return
     */
    private String assembleRegisterBody(RequestContext rc, String url, long ownerId, long agentId, RegisterReq req) {
        JSONObject object = new JSONObject();
        object.put("username", req.getUsername());
        object.put("password", req.getPassword());
        object.put("ip", rc.getIp());
        object.put("proxyId", agentId);
        object.put("ownerId", ownerId);
        object.put("sourceUrl", url);
        object.put("operatorTime", System.currentTimeMillis());
        object.put("appId", rc.getClient().getAppId());
        return object.toJSONString();
    }

    /**
     * 检查注册请求数据合法性
     *
     * @param req
     * @return
     */
    private boolean checkRegisterParam(RegisterReq req) {
        return Optional.ofNullable(req)
                .filter(request -> request.getUsername() != null && req.getUsername().length() >= 6 && req.getUsername().length() <= 16)
                .filter(request -> request.getPassword() != null && request.getPassword().length() == 32)
                .isPresent();
    }

    /**
     * 会员登陆
     *
     * @param rc       RequestContext
     * @param agent    浏览器数据
     * @param url      url
     * @param username 用户名
     * @param password 密码
     * @param code     验证码
     * @return
     */
    public String memberLogin(RequestContext rc, String agent, String url, String username, String password, String code) {
        if (!checkLoginReq(username, password)) {
            throw UserException.ILLEDGE_USERNAME_PASSWORD;
        }
        //todo andy 根据url获取业主ID
        OwnerInfo ownerInfo = domainDubboService.getOwnerInfoByDomain(url);
        if (ownerInfo == null || ownerInfo.getOwnerId() < 0) {
            throw UserException.ILLEGAL_SOURCE_URL;
        }
        //todo 从redis里面获取ownerId_username下的验证码，如果存在验证码，与code对比，如果相符，进行下一步操作
        String proCode = "";
        if (!proCode.equals(code)) {
            throw UserException.PROCODE_ERROR;
        }
        String body = assembleLoginBody(rc, ownerInfo.getOwnerId(), username, password, agent, url);
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100002, body);
        EGResp resp = thriftFactory.call(req, "account");
        if (resp == null || resp.getCode() == 0x1011) {
            throw UserException.MEMBER_LOGIN_FAIL;
        }
        int respCode = resp.getCode();
        if (respCode == 0x1008) {
            throw UserException.USERNAME_NOT_EXIST;
        }
        if (respCode == 0x1009) {
            //todo 记录ownerId_username输入密码错误数，如果错误数超过3次，则生成code，并返回
            throw UserException.PASSWORD_ERROR;
        }
        if (respCode != 0x2222) {
            throw UserException.MEMBER_LOGIN_FAIL;
        }
        JSONObject object = JSONObject.parseObject(resp.getData());
        long uid = object.getLongValue("uid");
        String token = object.getString("token");
        sendLoginMessage(ownerInfo.getOwnerId(), uid, rc.getIp());
        //todo 组装返回数据（需参考前端页面）
        return "{\" token: \"" + "\"" + resp.getData() + "\"}";
    }

    /**
     * 发送登陆成功消息
     *
     * @param ownerId
     * @param memberId
     * @param ip
     * @return
     */
    private boolean sendLoginMessage(long ownerId, long memberId, String ip) {
        try {
            HashMap<String, Object> map = new HashMap<>();
            map.put("ownerId", ownerId);
            map.put("memberId", memberId);
            map.put("ip", ip);
            map.put("loginTime", System.currentTimeMillis());
            return producer.send(Topic.MEMBER_LOGIN_SUCCESS, String.valueOf(memberId), JSON.toJSONString(map));
        } catch (Exception e) {
            ApiLogger.error(String.format("send member login success mq message error. ownerId: %d, memberId: %d", ownerId, memberId), e);
            return false;
        }
    }

    /**
     * 组装登录passport请求body
     *
     * @param rc
     * @param ownerId
     * @param username
     * @param password
     * @param url
     * @param agent
     * @return
     */
    private String assembleLoginBody(RequestContext rc, long ownerId, String username, String password, String agent, String url) {
        JSONObject object = new JSONObject();
        object.put("ownerId", ownerId);
        object.put("username", username);
        object.put("password", password);
        object.put("ip", rc.getIp());
        object.put("appId", rc.getClient().getAppId());
        object.put("loginUrl", url);
        object.put("deviceId", rc.getClient().getDeviceId());
        object.put("ext", new JSONObject().put("user-agent", agent).toString());
        object.put("operatorTime", System.currentTimeMillis() / 1000);


        return object.toJSONString();
    }

    /**
     * 检查用户名和密码有效性
     *
     * @param username
     * @param password
     * @return
     */
    private boolean checkLoginReq(String username, String password) {
        if (username == null || username.length() < 6 || username.length() > 16) {
            return false;
        }
        if (password == null || password.length() != 32) {
            return false;
        }
        return true;
    }

    /**
     * 密码重置
     *
     * @param rc          RequestContext
     * @param username    用户号
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return
     */
    public String memberPasswordReset(RequestContext rc, String username, String oldPassword, String newPassword) {
        if (!checkResetParams(username, oldPassword, newPassword)) {
            throw UserException.PASSWORD_RESET_FAIL;
        }
        String body = assembleResetBody(rc, username, oldPassword, newPassword);
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100004, body);
        EGResp resp = thriftFactory.call(req, "account");
        if (resp == null) {
            throw UserException.PASSWORD_RESET_FAIL;
        }
        int code = resp.getCode();
        if (code == 0x1009) {
            throw UserException.PASSWORD_ERROR;
        }
        if (code != 0x4444) {
            throw UserException.PASSWORD_RESET_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * 组装密码重置passport请求body
     *
     * @param rc
     * @param username
     * @param oldPassword
     * @param newPassword
     * @return
     */
    private String assembleResetBody(RequestContext rc, String username, String oldPassword, String newPassword) {
        JSONObject object = new JSONObject();
        object.put("userId", rc.getUid());
        object.put("username", username);
        object.put("oldPassword", oldPassword);
        object.put("newPassword", newPassword);
        object.put("appId", rc.getClient().getAppId());
        object.put("ip", rc.getIp());
        object.put("operatorTime", System.currentTimeMillis());
        return object.toJSONString();
    }

    /**
     * 密码重置参数检测
     *
     * @param username
     * @param oldPassword
     * @param newPassword
     * @return
     */
    private boolean checkResetParams(String username, String oldPassword, String newPassword) {
        if (StringUtils.isEmpty(username) || username.length() < 6 || username.length() > 16) {
            return false;
        }
        if (StringUtils.isEmpty(oldPassword) || oldPassword.length() != 32) {
            return false;
        }
        if (StringUtils.isEmpty(newPassword) || newPassword.length() != 32) {
            return false;
        }
        return true;
    }

    /**
     * 登陆状态检测
     *
     * @param rc
     * @return
     */
    public String memberLoginVerify(RequestContext rc) {
        String body = assembleVerifyBody(rc);
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100003, body);
        EGResp resp = thriftFactory.call(req, "account");
        boolean result = Optional.ofNullable(resp).filter(response -> response.getCode() != 0x3333).isPresent();
        if (!result) {
            throw UserException.VERIFY_FAIL;
        }
        return "{\" username: \"" + "\"" + resp.getData() + "\"}";
    }

    /**
     * 组装登陆校验passport 请求body
     *
     * @param rc RequestContext
     * @return
     */
    private String assembleVerifyBody(RequestContext rc) {
        JSONObject object = new JSONObject();
        object.put("userId", rc.getUid());
        object.put("operatorTime", System.currentTimeMillis());
        return object.toJSONString();
    }

    /**
     * 登陆注销
     *
     * @param rc
     * @param username
     * @return
     */
    public String memberLogout(RequestContext rc, String username) {
        if (username == null || username.length() < 6 || username.length() > 16) {
            throw UserException.ILLEGAL_PARAMETERS;
        }
        String body = assembleLogoutBody(rc, username);
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100005, body);
        EGResp resp = thriftFactory.call(req, "account");
        if (resp == null) {
            throw UserException.LOGOUT_FAIL;
        }
        int code = resp.getCode();
        if (code != 0x5555 || code != 0x1013) {
            throw UserException.LOGOUT_FAIL;
        }
        //消息发送
        sendLogoutMessage(rc.getUid());
        return UserContants.EMPTY_STRING;
    }

    /**
     * 发送注销成功消息
     *
     * @param memberId
     * @return
     */
    private boolean sendLogoutMessage(long memberId) {
        try {
            return producer.send(Topic.MEMBER_LOGOUT_SUCCESS, String.valueOf(memberId), String.valueOf(memberId));
        } catch (Exception e) {
            ApiLogger.error(String.format("send member logout success mq message error. memberId: %d", memberId), e);
            return false;
        }
    }

    /**
     * 组装注销passport 请求body
     *
     * @param rc
     * @param username
     * @return
     */
    private String assembleLogoutBody(RequestContext rc, String username) {
        JSONObject object = new JSONObject();
        object.put("userId", rc.getUid());
        object.put("username", username);
        object.put("deviceId", rc.getClient().getDeviceId());
        object.put("operatorTime", System.currentTimeMillis());
        return object.toJSONString();
    }

    /**
     * 在线会员列表
     *
     * @param rc        RequestContext
     * @param condition 检索条件
     * @param page      翻页
     * @param count     总数
     * @return
     */
    public String onlineList(RequestContext rc, String condition, Integer page, Integer count) {
        long uid = rc.getUid();
        User user = userService.getUserById(uid);
        if (page == null && page <= 0) page = 1;
        if (count == null && count <= 0) count = 10;
        if (user == null) {
            return JSON.toJSONString(assemblePage(page, count, 0, null));
        }
        OnlineMemberConditon memberCondition = parseContion(condition, user);
        long total = memberMongoService.getOnlineMemberCount(memberCondition);
        if (total <= 0) {
            return JSON.toJSONString(assemblePage(page, count, 0, null));
        }
        List<OnLineMember> list = memberMongoService.getOnlineMembers(memberCondition, page, count);
        return JSON.toJSONString(assemblePage(page, count, total, assembleOnlineMemberVo(list)));
    }

    /**
     * 组装在线会员列表
     *
     * @param list
     * @return
     */
    private Collection<OnLineMemberVo> assembleOnlineMemberVo(List<OnLineMember> list) {
        List<OnLineMemberVo> members = new ArrayList<>();
        Iterator<OnLineMember> iterator = list.iterator();
        while (iterator.hasNext()) {
            OnLineMember next = iterator.next();
            if (next != null) {
                OnLineMemberVo onLineMemberVo = new OnLineMemberVo();
                onLineMemberVo.setMemberId(next.getMemberId());
                onLineMemberVo.setAccount(next.getAccount());
                if (next.getLoginTime() != null) {
                    onLineMemberVo.setLoginTime(CommonDateParseUtil.date2string(new Date(next.getLoginTime()), CommonDateParseUtil.YYYY_MM_DD_HH_MM_SS));
                }
                onLineMemberVo.setRegisterTime(CommonDateParseUtil.date2string(new Date(next.getRegisterTime()), CommonDateParseUtil.YYYY_MM_DD_HH_MM_SS));
                onLineMemberVo.setLoginIp(next.getLoginIp());
                onLineMemberVo.setRegisterIp(next.getRegisterIp());
                members.add(onLineMemberVo);
            }
        }
        return members;
    }

    /**
     * 解析
     *
     * @param condition
     * @param user
     * @return
     */
    private OnlineMemberConditon parseContion(String condition, User user) {
        OnlineMemberConditon memberCondition = null;
        if (condition != null) {
            try {
                memberCondition = JSON.parseObject(condition, OnlineMemberConditon.class);
            } catch (Exception e) {
                ApiLogger.error(String.format("parse online condition error. condition: %s, msg: %s", condition, e.getMessage()));
            }
        }
        if (memberCondition == null) {
            memberCondition = new OnlineMemberConditon();
        }
        long uid = user.getUserId();
        AccountType type = user.getType();
        if (type == AccountType.proprietor) {
            memberCondition.setOwnerId(uid);
        } else if (type == AccountType.stockholder) {
            memberCondition.setHolderId(uid);
        } else if (type == AccountType.agent) {
            memberCondition.setAgentId(uid);
        } else {
            memberCondition.setOwnerId(uid);
        }
        return memberCondition;
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
    private static PageBean<OnLineMemberVo> assemblePage(int page, int count, long total, Collection<OnLineMemberVo> list) {
        PageBean<OnLineMemberVo> result = new PageBean<>();
        result.setPage(page);
        result.setCount(count);
        result.setTotal(total);
        result.setList(list);
        return result;
    }

    /**
     * 在线会员数
     *
     * @param rc
     * @return
     */
    public String onlineCount(RequestContext rc) {
        long uid = rc.getUid();
        User user = userService.getUserById(uid);
        if (user == null) {
            throw UserException.ILLEGAL_USER;
        }
        OnlineMemberConditon conditon = parseContion(null, user);
        conditon.setStatus(LoginType.login.value());
        long count = memberMongoService.getOnlineMemberCount(conditon);
        return "{\" count: \"" + count + "}";
    }
}
