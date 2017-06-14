package com.magic.user.member.resource.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.core.tools.MD5Util;
import com.magic.api.commons.model.PageBean;
import com.magic.api.commons.model.SimpleListResult;
import com.magic.api.commons.mq.Producer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.api.commons.tools.CommonDateParseUtil;
import com.magic.api.commons.tools.DateUtil;
import com.magic.api.commons.tools.IPUtil;
import com.magic.api.commons.tools.UUIDUtil;
import com.magic.api.commons.utils.StringUtils;
import com.magic.bc.query.vo.UserLevelVo;
import com.magic.config.thrift.base.EGResp;
import com.magic.config.vo.OwnerInfo;
import com.magic.passport.po.SubAccount;
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
import com.magic.user.service.*;
import com.magic.user.service.dubbo.DubboOutAssembleServiceImpl;
import com.magic.user.service.thrift.ThriftOutAssembleServiceImpl;
import com.magic.user.util.ExcelUtil;
import com.magic.user.util.UserUtil;
import com.magic.user.vo.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
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
    private LoginService loginService;

    @Resource
    private AccountIdMappingService accountIdMappingService;

    @Resource
    private MemberMongoService memberMongoService;

    @Resource
    private Producer producer;

    @Resource
    private ThriftOutAssembleServiceImpl thriftOutAssembleService;

    @Resource
    private DubboOutAssembleServiceImpl dubboOutAssembleService;

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
        if (memberCondition == null) {
            memberCondition = new MemberCondition();
        }
        memberCondition.setOwnerId(operaUser.getOwnerId());
        if (!checkCondition(memberCondition)) {
            return JSON.toJSONString(assemblePageBean(page, count, 0, null));
        }
        long total = memberMongoService.getCount(memberCondition);
        if (total <= 0) {
            return JSON.toJSONString(assemblePageBean(page, count, 0, null));
        }
        //获取mongo中查询到的会员列表
        List<MemberConditionVo> memberConditionVos = memberMongoService.queryByPage(memberCondition, page, count);
        //todo 组装mongo中拿取的列表
        List<MemberListVo> memberVos = assembleMemberVos(memberConditionVos);
        return JSON.toJSONString(assemblePageBean(page, count, total, memberVos));
    }

    /**
     * 组装会员列表
     *
     * @param memberConditionVos
     * @return
     */
    //TODO 调整，避免空指针
    private List<MemberListVo> assembleMemberVos(List<MemberConditionVo> memberConditionVos) {
        Map<Long, MemberConditionVo> memberConditionVoMap = new HashMap<>();
        for (MemberConditionVo vo : memberConditionVos) {
            memberConditionVoMap.put(vo.getMemberId(), vo);
        }
        List<MemberListVo> memberListVos = Lists.newArrayList();
        //1、获取会员基础信息
        List<Member> members = memberService.findMemberByIds(memberConditionVoMap.keySet());
        //2、获取会员最近登录信息
        Map<Long, SubAccount> subLogins = dubboOutAssembleService.getSubLogins(memberConditionVoMap.keySet());
        //3.获取会员层级、余额列表
        Map<Long, MemberListVo> memberBalanceLevelVoMap = getMemberBalances(memberConditionVoMap.keySet());
        //4.获取会员反水方案列表
        Map<Integer, MemberListVo> memberRetWaterMap = getMemberReturnWater(memberConditionVoMap.keySet());
        for (Member member : members) {
            MemberListVo memberListVo = new MemberListVo();
            memberListVo.setId(member.getMemberId());
            memberListVo.setAccount(member.getUsername());
            memberListVo.setAgentId(member.getAgentId());
            memberListVo.setAgent(member.getAgentUsername());
            memberListVo.setRegisterTime(DateUtil.formatDateTime(new Date(member.getRegisterTime()),DateUtil.formatDefaultTimestamp));
            memberListVo.setStatus(member.getStatus().value());
            memberListVo.setShowStatus(member.getStatus().desc());
            if (subLogins != null) {
                SubAccount subAccount = subLogins.get(member.getMemberId());
                if (subAccount != null) {
                    memberListVo.setLastLoginTime(DateUtil.formatDateTime(new Date(subAccount.getLastTime()), DateUtil.formatDefaultTimestamp));
                }
            } else {
                //todo 假数据删除
                memberListVo.setLastLoginTime("2017-04-17 18:03:22");
            }
            //todo 1、会员层级、余额在 kevin 拿取
            MemberListVo memberBalanceLevelVo = memberBalanceLevelVoMap.get(member.getMemberId());
            if (memberBalanceLevelVo != null) {
                memberListVo.setLevel(memberBalanceLevelVo.getLevel());
                memberListVo.setBalance(memberBalanceLevelVo.getBalance());
            } else {
                //TODO 假数据删除
                memberListVo.setLevel("未分层");
                memberListVo.setBalance("29843.23");
            }
            //todo 2、当前反水方案在 jason 拿取,根据层级id获取
            MemberListVo memberRetWaterVo = memberRetWaterMap.get(memberConditionVoMap.get(member.getMemberId()).getLevel());
            if (memberRetWaterVo != null) {
                memberListVo.setReturnWater(memberRetWaterVo.getReturnWater());
                memberListVo.setReturnWaterName(memberRetWaterVo.getReturnWaterName());
            } else {
                memberListVo.setReturnWater(1);
                memberListVo.setReturnWaterName("反水基本方案1");
            }

            memberListVos.add(memberListVo);
        }
        //TODO foreach list for item
        return memberListVos;
    }

    /**
     * 获取会员的余额列表
     * @param ids
     * @return
     */
    private Map<Long, MemberListVo> getMemberBalances(Collection<Long> ids) {
        JSONObject memberBalanceBody = new JSONObject();
        memberBalanceBody.put("memberIds", ids);
        EGResp balanceResp = thriftOutAssembleService.getMemberBalances(memberBalanceBody.toJSONString(), "account");
        //TODO 判断resp的code
        if (balanceResp != null && balanceResp.getData()!= null) {
            JSONArray balanceObj = JSONObject.parseArray(balanceResp.getData());
            Map<Long, MemberListVo> memberBalanceLevelVoMap = new HashMap<>();
            for (Object object : balanceObj) {
                JSONObject jsonObject = (JSONObject) object;
                MemberListVo vo = new MemberListVo();
                vo.setId(jsonObject.getLong("id"));
                vo.setLevel(jsonObject.getString("level"));
                vo.setBalance(jsonObject.getString("balance"));
                memberBalanceLevelVoMap.put(jsonObject.getLong("id"), vo);
            }
            return memberBalanceLevelVoMap;
        }
        return new HashMap<>();
    }

    /**
     * 组装会员反水记录列表
     * @param levelIds
     * @return
     */
    private Map<Integer, MemberListVo> getMemberReturnWater(Collection<Long> levelIds) {
        JSONObject memberRetWaterBody = new JSONObject();
        memberRetWaterBody.put("level", levelIds);
        EGResp retWaterResp = thriftOutAssembleService.getMemberBalances(memberRetWaterBody.toJSONString(), "account");
        //TODO 判断resp的code
        if (retWaterResp != null&& retWaterResp.getData()!= null) {
            JSONArray balanceObj = JSONObject.parseArray(retWaterResp.getData());
            Map<Integer, MemberListVo> memberBalanceLevelVoMap = new HashMap<>();
            for (Object object : balanceObj) {
                JSONObject jsonObject = (JSONObject) object;
                MemberListVo vo = new MemberListVo();
                vo.setReturnWater(jsonObject.getInteger("returnWater"));
                vo.setReturnWaterName(jsonObject.getString("returnWaterName"));
                memberBalanceLevelVoMap.put(jsonObject.getInteger("level"), vo);
            }
            return memberBalanceLevelVoMap;
        }
        return new HashMap<>();
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
            if (type == null || AccountType.parse(type) == null && (type != AccountType.agent.value() || type != AccountType.member.value())) {
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
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            throw UserException.ILLEGAL_USER;
        }
        String filename = ExcelUtil.assembleFileName(operaUser.getUserId(), ExcelUtil.MEMBER_LIST);
        DownLoadFile downLoadFile = new DownLoadFile();
        downLoadFile.setFilename(filename);
        byte[] content = new byte[0];
        MemberCondition memberCondition = MemberCondition.valueOf(condition);
        memberCondition.setOwnerId(operaUser.getOwnerId());
        if (!checkCondition(memberCondition)) {
            downLoadFile.setContent(content);
            return downLoadFile;
        }
        long total = memberMongoService.getCount(memberCondition);
        if (total <= 0) {
            downLoadFile.setContent(content);
            return downLoadFile;
        }
        //获取mongo中查询到的会员列表
        List<MemberConditionVo> memberConditionVos = memberMongoService.queryByPage(memberCondition, null, null);
        //todo 组装mongo中拿取的列表
        List<MemberListVo> memberVos = assembleMemberVos(memberConditionVos);
        //TODO 查询表数据，生成excel的zip，并返回zip byte[]
        content = ExcelUtil.memberListExport(memberVos, filename);
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
        SubAccount subAccount = dubboOutAssembleService.getSubLoginById(member.getMemberId());

        if (member == null) {
            throw UserException.ILLEGAL_MEMBER;
        }
        //TODO 1.jason 根据会员ID查询会员优惠方案
        //TODO 2.kevin 根据会员ID查询会员资金概括

        //TODO 3.sundy 根据会员ID查询投注记录
        //TODO 4.sundy 根据会员ID查询优惠记录
        MemberDetailVo detail = assembleMemberDetail(member);
        return JSON.toJSONString(detail);
    }

    /**
     * 组装会员详情 //TODO 暂未完成
     *
     * @return
     */
    private MemberDetailVo assembleMemberDetail(Member member) {
        MemberDetailVo vo = new MemberDetailVo();
        ////会员基础信息
        vo.setBaseInfo(assembleMemberInfo(member));

        /**
         * 会员优惠方案
         */
        MemberPreferScheme memberPreferScheme;
        //todo 1、会员优惠方案去 jason 那边拿取
        String privilegeBody = "{\"memberId\":" + member.getMemberId() + "}";
        EGResp privilegeResp = thriftOutAssembleService.getMemberPrivilege(privilegeBody, "account");
        //TODO 确定resp的code
        if (privilegeResp != null && privilegeResp.getData() != null) {
            JSONObject privilegeData = JSONObject.parseObject(privilegeResp.getData());
            memberPreferScheme = new MemberPreferScheme();
            memberPreferScheme.setLevel(privilegeData.getInteger("level"));
            memberPreferScheme.setShowLevel(privilegeData.getString("showLevel"));
            memberPreferScheme.setOnlineDiscount(privilegeData.getString("onlineDiscount"));
            memberPreferScheme.setReturnWater(privilegeData.getString("returnWater"));
            memberPreferScheme.setDepositDiscountScheme(privilegeData.getString("depositDiscountScheme"));
            //TODO 出款手续费、入款手续费 再次确定

        } else {
            //TODO 假数据去掉
            String preferScheme = "{\n" +
                    "\"level\": 1,\n" +
                    "\"showLevel\": \"未分层\",\n" +
                    "\"onlineDiscount\": \"100返10\",\n" +
                    "\"depositFee\": \"无\",\n" +
                    "\"withdrawFee\": \"无\",\n" +
                    "\"returnWater\": \"返水基本1\",\n" +
                    "\"depositDiscountScheme\": \"100返10\"\n" +
                    "}";
            memberPreferScheme = JSONObject.parseObject(preferScheme, MemberPreferScheme.class);
        }
        vo.setPreferScheme(memberPreferScheme);


        /**
         * 会员资金概况
         */
        MemberFundInfo memberFundInfoObj;
        FundProfile fundProfile = new FundProfile();
        //TODO 2、kevin 根据会员ID查询会员资金概括
        String capitalBody = "{\"memberId\":" + member.getMemberId() + "}";
        EGResp capitalResp = thriftOutAssembleService.getMemberCapital(capitalBody, "account");
        //TODO 确定resp的code
        if (capitalResp != null && capitalResp.getData() != null) {
            JSONObject capitalData = JSONObject.parseObject(capitalResp.getData());
            fundProfile.setSyncTime(capitalData.getString("syncTime"));
            memberFundInfoObj = new MemberFundInfo();
            memberFundInfoObj.setBalance(capitalData.getString("balance"));
            memberFundInfoObj.setLastDeposit(capitalData.getString("lastDeposit"));
            memberFundInfoObj.setLastWithdraw(capitalData.getString("lastWithdraw"));
            //TODO 存款总金额、取款总金额、存款总次数、取款总次数在mongo中获取

        } else {
            //TODO 假数据去掉
            String memberFundInfo = "{\n" +
                    "\t\"balance\": \"1805.50\",\n" +
                    "\t\"depositNumbers\": 15,\n" +
                    "\t\"depositTotalMoney\": \"29006590\",\n" +
                    "\t\"lastDeposit\": \"1200\",\n" +
                    "\t\"withdrawNumbers\": 10,\n" +
                    "\t\"withdrawTotalMoney\": \"24500120\",\n" +
                    "\t\"lastWithdraw\": \"2500\"\n" +
                    "}";
            memberFundInfoObj = JSONObject.parseObject(memberFundInfo, MemberFundInfo.class);
            fundProfile.setSyncTime("2017-05-31 09:12:35");
        }
        fundProfile.setInfo(memberFundInfoObj);
        vo.setFundProfile(fundProfile);


        /**
         * TODO 3.sundy 根据会员ID查询投注记录
         */
        String memberBetHistory = "{\n" +
                "\t\"totalMoney\": \"29000\",\n" +
                "\t\"effMoney\": \"28000\",\n" +
                "\t\"gains\": \"18000\"\n" +
                "}";
        MemberBetHistory memberBetHistoryObj = JSONObject.parseObject(memberBetHistory, MemberBetHistory.class);
        vo.setBetHistory(memberBetHistoryObj);
        /**
         * TODO 4.sundy 根据会员ID查询优惠记录
         */
        String memberDiscountHistory = "{\n" +
                "\t\"totalMoney\": \"1350\",\n" +
                "\t\"numbers\": 98,\n" +
                "\t\"returnWaterTotalMoney\": \"1450\"\n" +
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
        info.setTelephone(member.getTelephone());
        info.setType(AccountType.member.value());
        SubAccount subAccount = dubboOutAssembleService.getSubLoginById(member.getMemberId());
        if (subAccount != null) {
            info.setLastLoginIp(IPUtil.intToIp(subAccount.getLastIp()));
        }
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
        EGResp resp = thriftOutAssembleService.passwordReset(body, "account");
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
        body.put("userId", member.getMemberId());
        body.put("username", member.getUsername());
        body.put("newPassword", password);
        body.put("appId", rc.getClient().getAppId());
        body.put("ip", rc.getIp());
        body.put("operatorTime", System.currentTimeMillis());
        return body.toJSONString();
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
        EGResp resp = thriftOutAssembleService.logout(logoutBody(rc, member), "account");
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
        body.put("userId", member.getMemberId());
        body.put("username", member.getUsername());
        body.put("deviceId", StringUtils.isEmpty(rc.getClient().getDeviceId()) ? "default_deviceId" : rc.getClient().getDeviceId());
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
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            return UserContants.EMPTY_LIST;
        }
        SimpleListResult<List<MemberLevelListVo>> result = new SimpleListResult<>();
        //TODO jason dubbo
        List<MemberLevelListVo> list = getMemberLevelList(operaUser.getOwnerId(), lock, page, count);
        result.setList(list != null ? list : new ArrayList<>());
//        return JSON.toJSONString(result);
        //TODO 假数据
        return "{\n" +
                "    \"list\": [\n" +
                "        {\n" +
                "            \"id\": 10001,\n" +
                "            \"name\": \"VIP1\",\n" +
                "            \"createTime\": \"2017-03-01 16:43:22\",\n" +
                "            \"members\": 4310,\n" +
                "            \"condition\": {\n" +
                "                \"depositNumbers\": 1,\n" +
                "                \"depositTotalMoney\": \"1\",\n" +
                "                \"maxDepositMoney\": \"0\",\n" +
                "                \"withdrawNumbers\": 0,\n" +
                "                \"withdrawTotalMoney\": \"0\"\n" +
                "            },\n" +
                "            \"returnWater\": 1,\n" +
                "            \"returnWaterName\": \"返水方案1\",\n" +
                "            \"discount\": 1,\n" +
                "            \"discountName\": \"出入款优惠1\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 10002,\n" +
                "            \"name\": \"VIP2\",\n" +
                "            \"createTime\": \"2017-03-01 16:43:22\",\n" +
                "            \"members\": 4310,\n" +
                "            \"condition\": {\n" +
                "                \"depositNumbers\": 1,\n" +
                "                \"depositTotalMoney\": \"1\",\n" +
                "                \"maxDepositMoney\": \"0\",\n" +
                "                \"withdrawNumbers\": 0,\n" +
                "                \"withdrawTotalMoney\": \"0\"\n" +
                "            },\n" +
                "            \"returnWater\": 2,\n" +
                "            \"returnWaterName\": \"返水方案2\",\n" +
                "            \"discount\": 2,\n" +
                "            \"discountName\": \"出入款优惠2\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 10003,\n" +
                "            \"name\": \"VIP3\",\n" +
                "            \"createTime\": \"2017-03-01 16:43:22\",\n" +
                "            \"members\": 4310,\n" +
                "            \"condition\": {\n" +
                "                \"depositNumbers\": 1,\n" +
                "                \"depositTotalMoney\": \"1\",\n" +
                "                \"maxDepositMoney\": \"0\",\n" +
                "                \"withdrawNumbers\": 0,\n" +
                "                \"withdrawTotalMoney\": \"0\"\n" +
                "            },\n" +
                "            \"returnWater\": 3,\n" +
                "            \"returnWaterName\": \"返水方案3\",\n" +
                "            \"discount\": 3,\n" +
                "            \"discountName\": \"出入款优惠3\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 10004,\n" +
                "            \"name\": \"VIP4\",\n" +
                "            \"createTime\": \"2017-03-01 16:43:22\",\n" +
                "            \"members\": 4310,\n" +
                "            \"condition\": {\n" +
                "                \"depositNumbers\": 1,\n" +
                "                \"depositTotalMoney\": \"1\",\n" +
                "                \"maxDepositMoney\": \"0\",\n" +
                "                \"withdrawNumbers\": 0,\n" +
                "                \"withdrawTotalMoney\": \"0\"\n" +
                "            },\n" +
                "            \"returnWater\": 4,\n" +
                "            \"returnWaterName\": \"返水方案4\",\n" +
                "            \"discount\": 4,\n" +
                "            \"discountName\": \"出入款优惠4\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 10005,\n" +
                "            \"name\": \"VIP5\",\n" +
                "            \"createTime\": \"2017-03-01 16:43:22\",\n" +
                "            \"members\": 4310,\n" +
                "            \"condition\": {\n" +
                "                \"depositNumbers\": 1,\n" +
                "                \"depositTotalMoney\": \"1\",\n" +
                "                \"maxDepositMoney\": \"0\",\n" +
                "                \"withdrawNumbers\": 0,\n" +
                "                \"withdrawTotalMoney\": \"0\"\n" +
                "            },\n" +
                "            \"returnWater\": 5,\n" +
                "            \"returnWaterName\": \"返水方案5\",\n" +
                "            \"discount\": 5,\n" +
                "            \"discountName\": \"出入款优惠5\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 10006,\n" +
                "            \"name\": \"VIP6\",\n" +
                "            \"createTime\": \"2017-03-01 16:43:22\",\n" +
                "            \"members\": 4310,\n" +
                "            \"condition\": {\n" +
                "                \"depositNumbers\": 1,\n" +
                "                \"depositTotalMoney\": \"1\",\n" +
                "                \"maxDepositMoney\": \"0\",\n" +
                "                \"withdrawNumbers\": 0,\n" +
                "                \"withdrawTotalMoney\": \"0\"\n" +
                "            },\n" +
                "            \"returnWater\": 6,\n" +
                "            \"returnWaterName\": \"返水方案6\",\n" +
                "            \"discount\": 6,\n" +
                "            \"discountName\": \"出入款优惠6\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
    }

    /**
     * 在jason thrift获取层级列表
     * @param ownerId
     * @param lock
     * @return
     */
    private List<MemberLevelListVo> getMemberLevelList(Long ownerId, Integer lock, Integer page, Integer count) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ownerId", ownerId);
        jsonObject.put("lock", lock);
        jsonObject.put("page", page);
        jsonObject.put("count", count);
        EGResp resp = thriftOutAssembleService.findLevelList(jsonObject.toJSONString(), "account");
        //TODO 确定resp的code
        if (resp != null && resp.getData() != null) {
            List<MemberLevelListVo> memberLevelListVos = new ArrayList<>();
            JSONArray array = JSONArray.parseArray(resp.getData());
            for (Object object : array) {
                MemberLevelListVo memberLevelListVo = JSON.parseObject(JSON.toJSONString(object), MemberLevelListVo.class);
                memberLevelListVos.add(memberLevelListVo);
            }
            return memberLevelListVos;
        }
        return null;
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
        String filename = ExcelUtil.assembleFileName(uid, ExcelUtil.MEMBER_LEVEL_LIST);
        DownLoadFile downLoadFile = new DownLoadFile();
        downLoadFile.setFilename(filename);
        //TODO andy dubbo 查询表数据，生成excel的zip，并返回zip byte[]
        byte[] content = new byte[5];
        List<MemberLevelListVo> list = new ArrayList<>();

        downLoadFile.setContent(ExcelUtil.memberLevelListExport(list, filename));
        return downLoadFile;
    }

    /**
     * 会员层级映射列表
     * @return
     */
    public String levelListSimple(RequestContext rc) {
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            return UserContants.EMPTY_LIST;
        }
        SimpleListResult<List<UserLevelVo>> result = new SimpleListResult<>();
        //TODO andy dubbo
        List<UserLevelVo> list = dubboOutAssembleService.getLevelListSimple(operaUser.getOwnerId());
        result.setList(list != null ? list : new ArrayList<>());
        return JSON.toJSONString(result);
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
        Member member = memberService.getMemberById(id);
        if (member != null && member.getStatus().value() == status) {
            throw UserException.ILLEGAL_PARAMETERS;
        }
        member.setStatus(AccountStatus.parse(status));
        boolean result = memberService.updateStatus(member);
        if (!result) {
            throw UserException.MEMBER_STATUS_UPDATE_FAIL;
        }
        sendMemberStatusUpdateMq(id, status);
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
        if (!(accountStatus != AccountStatus.disable || accountStatus != AccountStatus.enable)) {
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

        OwnerInfo ownerInfo = dubboOutAssembleService.getOwnerInfoByDomain(url);
        if (ownerInfo == null || ownerInfo.getOwnerId() < 0) {
            throw UserException.ILLEGAL_SOURCE_URL;
        }

        if (!checkRegisterParam(req,ownerInfo.getOwnerId(),AccountType.member.value())) {
            throw UserException.ILLEGAL_PARAMETERS;
        }

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
        EGResp resp = thriftOutAssembleService.memberRegister(body, "account");
        if (resp == null) {
            throw UserException.REGISTER_FAIL;
        }
        int code = resp.getCode();
        if (code == 0x1004) {
            throw UserException.USERNAME_EXIST;
        }
        if (code != 0x1111) {
            throw UserException.REGISTER_FAIL;
        }
        long userId = 0l;
        try {
            userId = Long.parseLong(resp.getData());
        } catch (Exception e) {
            ApiLogger.error(String.format("passport register return data error. resp: %s", JSON.toJSONString(resp)), e);
        }
        if (userId <= 0) {
            throw UserException.REGISTER_FAIL;
        }
        Member member = assembleMember(rc, req, userId, ownerInfo.getOwnerId(), ownerInfo.getOwnerName(), holder, agent, url);
        boolean result = memberService.saveMember(member);
        if (!result) {
            throw UserException.REGISTER_FAIL;
        }
        sendRegisterMessage(member);
        return "{\"id\":" + userId + "}";
    }


    /**
     * 发送注册成功消息
     *
     * @param member
     * @return
     */
    private boolean sendRegisterMessage(Member member) {
        try {
            return producer.send(Topic.MEMBER_REGISTER_SUCCESS, String.valueOf(member.getMemberId()), JSON.toJSONString(member));
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
        member.setBankDeposit(req.getBankDeposit());
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
        member.setQq(req.getQq());
        member.setWeixin(req.getWeixin());
        member.setPaymentPassword(req.getPaymentPassword());
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
    private boolean checkRegisterParam(RegisterReq req,Long ownerId,int type) {
        //校验用户名和密码
        if (!Optional.ofNullable(req)
                .filter(request -> request.getUsername() != null && request.getUsername().length() >= 6
                        && request.getUsername().length() <= 16)
                .filter(request -> request.getPassword() != null && request.getPassword().length() == 32 )
                .isPresent()){
            return false;
        }
        //校验其他注册参数
        List<String> list = dubboOutAssembleService.getMustRegisterarameters(ownerId,type);
        if(list != null && list.size() > 0){
            for (String name : list) {
                try {
                    Field field = req.getClass().getField(name);
                    field.setAccessible(true);
                    if(field.get(req) == null) {
                        return false;
                    }
                    if("paymentPassword".equals(name)){
                        if(((String)field.get(req)).length() != 32){
                            return false;
                        }
                    }
                } catch (NoSuchFieldException e) {

                }catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 会员登陆
     *
     * @param rc       RequestContext
     * @param agent    浏览器数据
     * @param url      url
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @return
     */
    public String memberLogin(RequestContext rc, String agent, String url, String username, String password, String code) {
        if (!checkLoginReq(username, password)) {
            throw UserException.ILLEDGE_USERNAME_PASSWORD;
        }
        if (StringUtils.isEmpty(code)){
            throw UserException.VERIFY_CODE_ERROR;
        }
        String verifyCode = memberService.getVerifyCode(rc.getIp());
        if (StringUtils.isNotEmpty(verifyCode)){
            if (!verifyCode.toUpperCase().equals(code.toUpperCase())){
                throw UserException.VERIFY_CODE_ERROR;
            }
        }
        //根据url获取业主ID
        OwnerInfo ownerInfo = dubboOutAssembleService.getOwnerInfoByDomain(url);
        if (ownerInfo == null || ownerInfo.getOwnerId() < 0) {
            throw UserException.ILLEGAL_SOURCE_URL;
        }

        String body = assembleLoginBody(rc, ownerInfo.getOwnerId(), username, password, agent, url);
        EGResp resp = thriftOutAssembleService.memberLogin(body, "account");
        if (resp == null || resp.getCode() == 0x1011) {
            throw UserException.MEMBER_LOGIN_FAIL;
        }
        int respCode = resp.getCode();
        if (respCode == 0x1008) {
            throw UserException.USERNAME_NOT_EXIST;
        }
        if (respCode == 0x1009) {
            throw UserException.PASSWORD_ERROR;
        }
        if (respCode != 0x2222) {
            throw UserException.MEMBER_LOGIN_FAIL;
        }
        JSONObject object = JSONObject.parseObject(resp.getData());
        long uid = object.getLongValue("uid");
        //查询会员是否被停用
        Member member = memberService.getMemberById(uid);
        if (!Optional.ofNullable(member).isPresent()){
            throw UserException.ILLEGAL_USER;
        }
        if(member.getStatus() == AccountStatus.disable){
            throw UserException.MEMBER_ACCOUNT_DISABLED;
        }

        String token = object.getString("token");
        long message = dubboOutAssembleService.getNoReadMessageCount(uid);
        EGResp capitalResp = thriftOutAssembleService.getMemberCapital("{\"memberId\":" + uid + "}", "account");

        String result = assembleLoginResult(uid,member.getUsername(),token,message,capitalResp);
        sendLoginMessage(member, rc);

        return result;
    }

    /**
     * 组装返回数据
     *
     * @param uid
     * @param username
     * @param token
     * @param message
     * @param capitalResp 从中解析出账户余额
     * @return
     */
    private String assembleLoginResult(long uid, String username, String token, long message, EGResp capitalResp) {
        LoginSuccessInfoVo loginInfo = new LoginSuccessInfoVo();
        loginInfo.setId(uid);
        loginInfo.setUsername(username);
        loginInfo.setToken(token);
        loginInfo.setMessage(message);
        if (capitalResp != null && capitalResp.getData() != null) {
            JSONObject capitalData = JSONObject.parseObject(capitalResp.getData());
            if(capitalData.getString("balance") != null){
                loginInfo.setBalance(capitalData.getString("balance"));
            }else{
                loginInfo.setBalance("0");
            }
        }else{
            loginInfo.setBalance("0");
        }

        return JSONObject.toJSONString(loginInfo);
    }

    /**
     * 发送登陆成功消息
     *
     * @param member
     * @param rc
     * @return
     */
    private boolean sendLoginMessage(Member member, RequestContext rc) {
        try {
            HashMap<String, Object> map = new HashMap<>();
            map.put("member", member);
            map.put("ip", rc.getIp());
            map.put("loginTime", System.currentTimeMillis());
            return producer.send(Topic.MEMBER_LOGIN_SUCCESS, String.valueOf(member.getMemberId()), JSON.toJSONString(map));
        } catch (Exception e) {
            ApiLogger.error(String.format("send member login success mq message error. member: %s, ip: %s", JSON.toJSONString(member), rc.getIp()), e);
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
        object.put("deviceId", StringUtils.isEmpty(rc.getClient().getDeviceId()) ? "default_deviceId" : rc.getClient().getDeviceId());
        JSONObject userAgentObj = new JSONObject();
        userAgentObj.put("user-agent", agent);
        object.put("ext", userAgentObj);
        object.put("operatorTime", System.currentTimeMillis());


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
        EGResp resp = thriftOutAssembleService.memberPasswordReset(body, "account");
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
        EGResp resp = thriftOutAssembleService.memberLoginVerify(body, "account");
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
        EGResp resp = thriftOutAssembleService.memberLogout(body, "account");
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
        object.put("deviceId", StringUtils.isEmpty(rc.getClient().getDeviceId()) ? "default_deviceId" : rc.getClient().getDeviceId());
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
        memberCondition.setOwnerId(user.getOwnerId());
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
        conditon.setOwnerId(user.getOwnerId());
        conditon.setStatus(LoginType.login.value());
        long count = memberMongoService.getOnlineMemberCount(conditon);
        return "{\" count: \"" + count + "}";
    }

    /**
     * 查询个人中心详细信息
     *
     * @param rc
     * @return
     */
    public String memberCenterDetail (RequestContext rc){
        Member member = memberService.getMemberById(rc.getUid());
        if (member == null) {
            throw UserException.ILLEGAL_USER;
        }

        MemberCenterDetailVo memberCenterDetailVo = new MemberCenterDetailVo();
        SubAccount subAccount = dubboOutAssembleService.getSubLoginById(member.getMemberId());
        if (subAccount != null && subAccount.getLastTime() != 0) {
            memberCenterDetailVo.setLastLoginTime(DateUtil.formatDateTime(DateUtil.getDate(subAccount.getLastTime()), DateUtil.formatDefaultTimestamp));
        }

        initMemberCenterDetailVo(memberCenterDetailVo, member);

        return JSONObject.toJSONString(memberCenterDetailVo);
    }

    private void initMemberCenterDetailVo(MemberCenterDetailVo o,Member member){
        if(o == null || member == null){
            return;
        }
        if(StringUtils.isNotEmpty(member.getBankCardNo())){
            o.setBankCardNo(member.getBankCardNo());
        }else{
            o.setBankCardNo("尚未设置提款银行卡");
        }
        if(StringUtils.isNotEmpty(member.getWeixin())){
            o.setWeixin(member.getWeixin());
        }else{
            o.setWeixin("无");
        }
        if(StringUtils.isNotEmpty(member.getQq())){
            o.setQq(member.getQq());
        }else{
            o.setQq("无");
        }
        if(StringUtils.isNotEmpty(member.getUsername())){
            o.setUsername(member.getUsername());
        }else{
            o.setUsername("无");
        }
        if(StringUtils.isNotEmpty(member.getRealname())){
            o.setRealname(member.getRealname());
        }else{
            o.setRealname("无");
        }
        if(StringUtils.isNotEmpty(member.getEmail())){
            o.setEmail(member.getEmail());
        }else{
            o.setEmail("无");
        }
    }

    /**
     * 获取会员的余额和未读消息
     * @param rc
     * @return
     */
    public String getMemberInfo(RequestContext rc) {
        long message = dubboOutAssembleService.getNoReadMessageCount(rc.getUid());
        String balance = null;
        EGResp capitalResp = thriftOutAssembleService.getMemberCapital("{\"memberId\":" + rc.getUid() + "}", "account");
        if (capitalResp != null && capitalResp.getData() != null) {
            JSONObject capitalData = JSONObject.parseObject(capitalResp.getData());
            balance = capitalData.getString("balance");
        }
        if (StringUtils.isEmpty(balance)){
            //TODO 对接好数据后，修改为0
            balance = "3000";
        }
        return "{\"message\":"+message+",\"balance\":\""+balance+"\"}";
    }

    /**
     * 返回验证码
     *
     * @param rc
     * @return
     */
    public String getCode(RequestContext rc) {
        String code = UserUtil.checkCode();
        long ip = IPUtil.ipToLong(rc.getIp());
        ApiLogger.info(String.format("refresh code. ip: %d, code: %s", ip, code));
        boolean result = memberService.refreshCode(ip, code);
        if (!result){
            throw UserException.GET_VERIFY_CODE_ERROR;
        }
        return "{\"code\":" + "\"" + code + "\"" + "}";
    }

}
