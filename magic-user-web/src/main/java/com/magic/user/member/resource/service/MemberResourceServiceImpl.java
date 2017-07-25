package com.magic.user.member.resource.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.model.PageBean;
import com.magic.api.commons.model.SimpleListResult;
import com.magic.api.commons.mq.Producer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.api.commons.tools.IPUtil;
import com.magic.api.commons.tools.LocalDateTimeUtil;
import com.magic.api.commons.tools.NumberUtil;
import com.magic.api.commons.tools.UUIDUtil;
import com.magic.api.commons.utils.StringUtils;
import com.magic.bc.query.vo.UserLevelVo;
import com.magic.config.thrift.base.EGResp;
import com.magic.config.vo.OwnerInfo;
import com.magic.oceanus.entity.Summary.UserOrderRecord;
import com.magic.oceanus.entity.Summary.UserPreferentialRecord;
import com.magic.owner.entity.Resources;
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
import com.magic.user.service.AccountIdMappingService;
import com.magic.user.service.MemberMongoService;
import com.magic.user.service.MemberService;
import com.magic.user.service.UserService;
import com.magic.user.service.dubbo.DubboOutAssembleServiceImpl;
import com.magic.user.service.thrift.ThriftOutAssembleServiceImpl;
import com.magic.user.util.AuthConst;
import com.magic.user.util.ExcelUtil;
import com.magic.user.util.UserUtil;
import com.magic.user.vo.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MemberResourceServiceImpl
 *
 * @author zj
 * @date 2017/5/6
 */
@Service("memberServiceResource")
public class MemberResourceServiceImpl {
    //根据ip获取城市的接口地址
    private static final String URL = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=";
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
    private ThriftOutAssembleServiceImpl thriftOutAssembleService;

    @Resource
    private DubboOutAssembleServiceImpl dubboOutAssembleService;

    /**
     * 组装翻页数据
     *
     * @param page  页码
     * @param count 当页条数
     * @param total 总条数
     * @param list  详细列表数据
     * @return
     */
    private static PageBean<MemberListVo> assemblePageBean(Integer page, Integer count, Long total, Collection<MemberListVo> list) {
        PageBean<MemberListVo> result = new PageBean<>();
        result.setPage(page);
        result.setCount(count);
        result.setTotal(total);
        result.setList(list);
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
    private static PageBean<String> assemblePageBeanList(Integer page, Integer count, long total, Collection<String> list) {
        PageBean<String> result = new PageBean<>();
        result.setPage(page);
        result.setCount(count);
        result.setTotal(total);
        result.setList(list);
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
    private static PageBean<OnLineMemberVo> assemblePage(int page, int count, long total, Collection<OnLineMemberVo> list) {
        PageBean<OnLineMemberVo> result = new PageBean<>();
        result.setPage(page);
        result.setCount(count);
        result.setTotal(total);
        result.setList(list);
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
    private static PageBean<MemberConditionVo> assemblePageBeanMemberCondions(Integer page, Integer count, Long total, Collection<MemberConditionVo> list) {
        PageBean<MemberConditionVo> result = new PageBean<>();
        result.setPage(page);
        result.setCount(count);
        result.setTotal(total);
        result.setList(list);
        return result;
    }

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
            return JSON.toJSONString(assemblePageBean(page, count, 0L, null));
        }
        if (operaUser.getType() == AccountType.agent) {
            memberCondition.setAgentId(operaUser.getUserId());
        }
        long total = memberMongoService.getCount(memberCondition);
        if (total <= 0) {
            return JSON.toJSONString(assemblePageBean(page, count, 0L, null));
        }
        //获取mongo中查询到的会员列表
        List<MemberConditionVo> memberConditionVos = memberMongoService.queryByPage(memberCondition, page, count);
        ApiLogger.info(String.format("get member conditon from mongo. members: %s", JSON.toJSONString(memberConditionVos)));
        if (!Optional.ofNullable(memberConditionVos).filter(size -> size.size() > 0).isPresent()) {
            return JSON.toJSONString(assemblePageBean(page, count, 0L, null));
        }
        List<MemberListVo> memberVos = assembleMemberVos(memberConditionVos);
        return JSON.toJSONString(assemblePageBean(page, count, total, memberVos));
    }

    /**
     * 组装会员列表
     *
     * @param memberConditionVos
     * @return
     */
    private List<MemberListVo> assembleMemberVos(List<MemberConditionVo> memberConditionVos) {
        Set<Long> memberIds = memberConditionVos.stream().map(MemberConditionVo::getMemberId).collect(Collectors.toSet());
        List<MemberListVo> memberListVos = Lists.newArrayList();
        //1、获取会员基础信息
        Map<Long, Member> members = memberService.findMemberByIds(memberIds);
        ApiLogger.info(String.format("get members. ids: %s, result: %s", JSON.toJSONString(memberIds), JSON.toJSONString(members)));
        if (!Optional.ofNullable(members).filter(size -> size.size() > 0).isPresent()) {
            return memberListVos;
        }
        //2、获取会员最近登录信息
        Map<Long, SubAccount> subLogins = dubboOutAssembleService.getSubLogins(memberIds);
        //3.获取余额列表
        Map<Long, String> memberBalanceLevelVoMap = thriftOutAssembleService.getMemberBalances(memberIds);
        //4.获取会员反水方案列表
        Set<Long> levels = memberConditionVos.stream().map(MemberConditionVo::getLevel).collect(Collectors.toSet());
        Map<Long, MemberListVo> memberRetWaterMap = getMemberReturnWater(levels);
        ApiLogger.info(String.format("get return water scheme. levels: %s, result: %s", JSON.toJSONString(levels), JSON.toJSONString(memberRetWaterMap)));
        for (MemberConditionVo vo : memberConditionVos) {
            //1.组装基础信息
            Member member = members.get(vo.getMemberId());
            if (!Optional.ofNullable(member).isPresent()) {
                continue;
            }
            MemberListVo memberListVo = assembleMemberListVo(member);
            //2.登录时间
            memberListVo.setLastLoginTime(getLastLoginTime(vo.getMemberId(), subLogins));
            //3.会员余额
            memberListVo.setBalance(getMemberBalance(vo.getMemberId(), memberBalanceLevelVoMap));
            //4.返水方案
            MemberListVo returnWater = assembleMemberListVo(vo, memberRetWaterMap);
            if (!Optional.ofNullable(returnWater).isPresent()) {
                memberListVo.setReturnWater(0);
                memberListVo.setReturnWaterName("");
                memberListVo.setLevel("");
            } else {
                memberListVo.setReturnWater(returnWater.getReturnWater());
                memberListVo.setReturnWaterName(returnWater.getReturnWaterName());
                memberListVo.setLevel(returnWater.getLevel());
            }
            memberListVos.add(memberListVo);
        }
        return memberListVos;
    }

    /**
     * 组装返水方案
     *
     * @param vo
     * @param memberRetWaterMap
     * @return
     */
    private MemberListVo assembleMemberListVo(MemberConditionVo vo, Map<Long, MemberListVo> memberRetWaterMap) {
        if (!Optional.ofNullable(memberRetWaterMap).filter(size -> size.size() > 0).isPresent()) {
            return null;
        }
        MemberListVo memberListVo = memberRetWaterMap.get(vo.getLevel());
        if (!Optional.ofNullable(memberListVo).isPresent()) {
            return null;
        }
        MemberListVo result = new MemberListVo();
        result.setReturnWater(memberListVo.getReturnWater());
        result.setReturnWaterName(memberListVo.getReturnWaterName() == null ? "" : memberListVo.getReturnWaterName());
        result.setLevel(memberListVo.getLevel() == null ? "" : memberListVo.getLevel());
        return result;
    }

    /**
     * 会员余额
     *
     * @param memberId
     * @param map
     * @return
     */
    private String getMemberBalance(Long memberId, Map<Long, String> map) {
        if (!Optional.ofNullable(map).filter(size -> size.size() > 0).isPresent()) {
            return "0";
        }
        return map.getOrDefault(memberId, "0");
    }

    /**
     * 获取登陆时间
     *
     * @param memberId
     * @param subLogins
     * @return
     */
    private String getLastLoginTime(Long memberId, Map<Long, SubAccount> subLogins) {
        if (!Optional.ofNullable(subLogins).filter(size -> size.size() > 0).isPresent()) {
            return "";
        }
        SubAccount subAccount = subLogins.get(memberId);
        if (!Optional.ofNullable(subAccount).filter(time -> time.getLastTime() > 0).isPresent()) {
            return "";
        }
        return LocalDateTimeUtil.toAmerica(subAccount.getLastTime());
    }

    /**
     * 组装基础信息
     *
     * @param member
     * @return
     */
    private MemberListVo assembleMemberListVo(Member member) {
        MemberListVo vo = new MemberListVo();
        vo.setId(member.getMemberId());
        vo.setAccount(member.getUsername());
        vo.setAgentId(member.getAgentId());
        vo.setAgent(member.getAgentUsername());
        vo.setRegisterTime(LocalDateTimeUtil.toAmerica(member.getRegisterTime()));
        vo.setStatus(member.getStatus().value());
        vo.setShowStatus(member.getStatus().desc());
        return vo;
    }

    /**
     * 组装会员反水记录列表
     *
     * @param levelIds
     * @return
     */
    private Map<Long, MemberListVo> getMemberReturnWater(Set<Long> levelIds) {
        JSONObject memberRetWaterBody = new JSONObject();
        memberRetWaterBody.put("levels", levelIds);
        try {
            EGResp retWaterResp = thriftOutAssembleService.getMemberReturnWater(memberRetWaterBody.toJSONString(), "account");
            if (retWaterResp != null && retWaterResp.getData() != null) {
                JSONObject obj = JSONObject.parseObject(retWaterResp.getData());
                if (obj != null && obj.getInteger("total") > 0) {
                    JSONArray result = obj.getJSONArray("levels");
                    Map<Long, MemberListVo> memberBalanceLevelVoMap = new HashMap<>();
                    for (Object object : result) {
                        JSONObject jsonObject = (JSONObject) object;
                        MemberListVo vo = new MemberListVo();
                        //vo.setReturnWater(jsonObject.getInteger("returnWater"));
                        vo.setReturnWaterName(jsonObject.getString("returnWater"));
                        vo.setLevel(jsonObject.getString("showLevel"));
                        memberBalanceLevelVoMap.put(jsonObject.getLong("level"), vo);
                    }
                    return memberBalanceLevelVoMap;
                }
            }
        } catch (Exception e) {
            ApiLogger.error("get member return water failed!", e);
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

    public void authOfSearchResources(Long agentId, Long ownerId, Member member) {
        List<Resources> resources = dubboOutAssembleService.getUserRes(agentId, ownerId);
        boolean hasEmail = false, hasPhone = false, hasBankCardNo = false; //初始化未拥有权限
        if (resources.size() > 0) {
            for (Resources temp : resources) {
                if (temp.getEngKey().trim().equals(AuthConst.MEMBER_CHECK_PHONE_KEY)) {
                    hasEmail = true;
                }
                if (temp.getEngKey().trim().equals(AuthConst.MEMBER_CHECK_EMIAL_KEY)) {
                    hasPhone = true;
                }
                if (temp.getEngKey().trim().equals(AuthConst.MEMBER_CHECK_BANKCARDNO_KEY)) {
                    hasBankCardNo = true;
                }
            }
        }
        if (!hasEmail) {
            member.setEmail("************");
        }
        if (!hasPhone) {
            member.setTelephone("************");
        }
        if (!hasBankCardNo) {
            member.setBankCardNo("************");
        }
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
        if (member == null) {
            throw UserException.ILLEGAL_MEMBER;
        }
        // 权限检查
        authOfSearchResources(member.getMemberId(), member.getOwnerId(), member);

        MemberDetailVo detail = assembleMemberDetail(member);
        return JSON.toJSONString(detail);
    }

    /**
     * 组装会员详情
     *
     * @return
     */
    private MemberDetailVo assembleMemberDetail(Member member) {
        MemberDetailVo vo = new MemberDetailVo();
        vo.setBaseInfo(assembleMemberInfo(member));
        //从mongo查询会员详情
        MemberConditionVo mv = memberMongoService.get(member.getMemberId());
        vo.setPreferScheme(getPreferScheme(mv));
        vo.setFundProfile(assembleFundProfile(mv, member));
        vo.setBetHistory(assembleBetHistory(member));
        vo.setDiscountHistory(assembleDiscountHistory(member));
        return vo;
    }

    /**
     * 优惠记录
     *
     * @param member
     * @return
     */
    private MemberDiscountHistory assembleDiscountHistory(Member member) {
        MemberDiscountHistory history = new MemberDiscountHistory();
        UserPreferentialRecord operation = dubboOutAssembleService.getPreferentialOperation(member.getMemberId(), member.getStockId());
        if (Optional.ofNullable(operation).isPresent()) {
            history.setNumbers(operation.getNumbers() == null ? 0 : operation.getNumbers().intValue());
            history.setReturnWaterTotalMoney(operation.getReturnWaterTotalMoney() == null ? "0" : String.valueOf(NumberUtil.fenToYuan(operation.getReturnWaterTotalMoney())));
            history.setTotalMoney(operation.getTotalMoney() == null ? "0" : String.valueOf(NumberUtil.fenToYuan(operation.getTotalMoney())));
        } else {
            history.setNumbers(0);
            history.setReturnWaterTotalMoney("0");
            history.setTotalMoney("0");
        }
        return history;
    }

    /**
     * 投注记录
     *
     * @param member
     * @return
     */
    private MemberBetHistory assembleBetHistory(Member member) {
        MemberBetHistory history = new MemberBetHistory();
        UserOrderRecord operation = dubboOutAssembleService.getMemberOperation(member.getMemberId(), member.getStockId());
        if (Optional.ofNullable(operation).isPresent()) {
            history.setEffMoney(operation.getEffMoney() == null ? "0" : String.valueOf(NumberUtil.fenToYuan(operation.getEffMoney())));
            history.setTotalMoney(operation.getTotalMoney() == null ? "0" : String.valueOf(NumberUtil.fenToYuan(operation.getTotalMoney())));
            history.setGains(operation.getGains() == null ? "0" : String.valueOf(NumberUtil.fenToYuan(operation.getGains())));
        } else {
            history.setEffMoney("0");
            history.setGains("0");
            history.setTotalMoney("0");
        }
        return history;
    }

    /**
     * 组装会员资金概况
     *
     * @param mv
     * @param member
     * @return
     */
    private FundProfile assembleFundProfile(MemberConditionVo mv, Member member) {
        FundProfile<MemberFundInfo> fundProfile = new FundProfile<>();
        fundProfile.setSyncTime(LocalDateTimeUtil.toAmerica(System.currentTimeMillis()));
        MemberFundInfo fundInfo = new MemberFundInfo();
        fundInfo.setBalance(thriftOutAssembleService.getMemberBalance(member.getMemberId()));
        if (Optional.ofNullable(mv).isPresent()) {
            fundInfo.setDepositNumbers(mv.getDepositCount() == null ? 0 : mv.getDepositCount());//存款总次数
            fundInfo.setDepositTotalMoney(String.valueOf(mv.getDepositMoney() == null ? "0" : NumberUtil.fenToYuan(mv.getDepositMoney())));//存款总金额
            fundInfo.setLastDeposit(mv.getLastDepositMoney() == null ? "0" : String.valueOf(NumberUtil.fenToYuan(mv.getLastDepositMoney())));//最近存款
            fundInfo.setWithdrawNumbers(mv.getWithdrawCount() == null ? 0 : mv.getWithdrawCount());//取款总次数
            fundInfo.setWithdrawTotalMoney(mv.getWithdrawMoney() == null ? "0" : String.valueOf(NumberUtil.fenToYuan(mv.getWithdrawMoney())));//取款总金额
            fundInfo.setLastWithdraw(mv.getLastWithdrawMoney() == null ? "0" : String.valueOf(NumberUtil.fenToYuan(mv.getLastWithdrawMoney())));//最近取款
        } else {
            fundInfo.setDepositNumbers(0);//存款总次数
            fundInfo.setDepositTotalMoney("0");//存款总金额
            fundInfo.setLastDeposit("0");//最近存款
            fundInfo.setWithdrawNumbers(0);//取款总次数
            fundInfo.setWithdrawTotalMoney("0");//取款总金额
            fundInfo.setLastWithdraw("0");//最近取款
        }
        fundProfile.setInfo(fundInfo);
        return fundProfile;
    }

    /**
     * 优惠方案
     *
     * @param mv
     * @return
     */
    private MemberPreferScheme getPreferScheme(MemberConditionVo mv) {
        MemberPreferScheme result = new MemberPreferScheme();
        if (Optional.ofNullable(mv).isPresent()) {
            result = thriftOutAssembleService.getMemberPrivilege(mv.getLevel());
        }
        if (!Optional.ofNullable(result).isPresent()) {
            result = new MemberPreferScheme();
            result.setLevel(0);
            result.setShowLevel("无");
            result.setOnlineDiscount("无");
            result.setReturnWater("无");
            result.setDepositDiscountScheme("无");
        } else {
            if (!Optional.ofNullable(result.getLevel()).isPresent()) {
                result.setLevel(0);
            }
            if (StringUtils.isEmpty(result.getShowLevel())) {
                result.setShowLevel("无");
            }
            if (StringUtils.isEmpty(result.getOnlineDiscount())) {
                result.setOnlineDiscount("无");
            }
            if (StringUtils.isEmpty(result.getReturnWater())) {
                result.setReturnWater("无");
            }
            if (StringUtils.isEmpty(result.getDepositDiscountScheme())) {
                result.setDepositDiscountScheme("无");
            }
        }
        return result;
    }

    /**
     * @param member
     * @return
     * @Doc 组装会员基础信息中的会员信息
     */
    private MemberInfo assembleMemberInfo(Member member) {
        MemberInfo info = new MemberInfo();
        info.setId(member.getMemberId());
        info.setAccount(member.getUsername());
        info.setAgentId(member.getAgentId());
        info.setAgent(member.getAgentUsername());
        info.setRealname(member.getRealname());
        info.setRegisterTime(LocalDateTimeUtil.toAmerica(member.getRegisterTime()));
        info.setRegisterIp(IPUtil.intToIp(member.getRegisterIp()));
        info.setEmail(member.getEmail());
        info.setStatus(member.getStatus().value());
        info.setShowStatus(member.getStatus().desc());
        info.setBankCardNo(member.getBankCardNo());
        info.setTelephone(member.getTelephone());
        info.setType(AccountType.member.value());
        info.setBank(member.getBank());
        /*最近登录信息*/
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
            sendMemberStatusUpdateMq(id, status, member.getOwnerId());
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
     * @param memberId
     * @param level
     * @return
     */
    public String updateLevel(RequestContext rc, Long memberId, Long level) {
        if (!Optional.ofNullable(memberId).filter(id -> id > 0).isPresent()) {
            throw UserException.ILLEGAL_PARAMETERS;
        }
        if (!Optional.ofNullable(level).filter(levelValue -> levelValue > 0).isPresent()) {
            throw UserException.ILLEGAL_PARAMETERS;
        }
        Member member = memberService.getMemberById(memberId);
        if (member == null) {
            throw UserException.ILLEGAL_MEMBER;
        }
        boolean result = thriftOutAssembleService.setMemberLevel(member, level);
        if (result) {
            result = memberMongoService.updateLevel(member, level);
        }
        if (!result) {
            throw UserException.MEMBER_LEVEL_UPDATE_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * 会员层级列表
     *
     * @param rc   RequestContext
     * @param lock 是否锁定 1：非锁定 2锁定
     * @return
     */
    public String memberLevelList(RequestContext rc, int lock) {
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            return UserContants.EMPTY_LIST;
        }
        SimpleListResult<List<MemberLevelListVo>> result = new SimpleListResult<>();
        List<MemberLevelListVo> list = getMemberLevelList(operaUser.getOwnerId(), lock);
        result.setList(list != null ? list : new ArrayList<>());
        return JSON.toJSONString(result);
    }

    /**
     * 在jason thrift获取层级列表
     *
     * @param ownerId
     * @param lock
     * @return
     */
    private List<MemberLevelListVo> getMemberLevelList(Long ownerId, Integer lock) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ownerId", ownerId);
        jsonObject.put("isLocked", lock);
//        jsonObject.put("page", page);
//        jsonObject.put("count", count);
        EGResp resp = thriftOutAssembleService.findLevelList(jsonObject.toJSONString(), "account");
//        ApiLogger.info("==========thrift获取层级列表============");
//        ApiLogger.info(JSONObject.toJSONString(resp.getData()));
        if (resp != null && resp.getData() != null) {
            List<MemberLevelListVo> memberLevelListVos = new ArrayList<>();
            JSONArray array = JSONObject.parseObject(resp.getData()).getJSONArray("userLevels");
            for (Object object : array) {
                JSONObject js = (JSONObject) object;
                // MemberLevelListVo memberLevelListVo = JSON.parseObject(JSON.toJSONString(object), MemberLevelListVo.class);
                MemberLevelListVo memberLevelListVo = new MemberLevelListVo();
                memberLevelListVo.setId(js.getInteger("userLevel"));
                memberLevelListVo.setName(js.getString("userLevelName"));
                memberLevelListVo.setCreateTime(
                        LocalDateTimeUtil.toAmerica(Long.valueOf(js.getString("createTime"))));
                memberLevelListVo.setReturnWater(js.getInteger("cbsId"));
                memberLevelListVo.setReturnWaterName(js.getString("cbsName"));
                memberLevelListVo.setDiscount(js.getInteger("dwdsId"));
                memberLevelListVo.setDiscountName(js.getString("dwdsName"));
                memberLevelListVo.setMembers(js.getInteger("members"));

                JSONObject condition = js.getJSONObject("condition");
                LevelCondition llc = new LevelCondition();
                llc.setDepositNumbers(condition.getInteger("depositTimes"));
                llc.setDepositTotalMoney(condition.getString("despositTotalAmount"));
                llc.setMaxDepositMoney(condition.getString("withdrawTotalAmount"));
                llc.setWithdrawNumbers(condition.getInteger("withdrawalTimes"));
                llc.setWithdrawTotalMoney(condition.getString("withdrawTotalAmount"));

                memberLevelListVo.setCondition(llc);
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

        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            throw UserException.ILLEGAL_USER;
        }

        List<MemberLevelListVo> list = getMemberLevelList(operaUser.getOwnerId(), lock);

        downLoadFile.setContent(ExcelUtil.memberLevelListExport(list, filename));
        return downLoadFile;
    }

    /**
     * 会员层级映射列表
     *
     * @return
     */
    public String levelListSimple(RequestContext rc) {
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            return UserContants.EMPTY_LIST;
        }
        SimpleListResult<List<UserLevelVo>> result = new SimpleListResult<>();

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
        sendMemberStatusUpdateMq(id, status, member.getOwnerId());
        return UserContants.EMPTY_STRING;
    }

    /**
     * @param id
     * @param status
     * @Doc 发送会员状态修改
     */
    private void sendMemberStatusUpdateMq(Long id, Integer status, Long ownerId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("memberId", id);
        jsonObject.put("status", status);
        jsonObject.put("ownerId", ownerId);
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
     * @param rc         RequestContext
     * @param url        注册来源
     * @param req        注册请求数据
     * @param verifyCode 验证码
     * @return
     */
    public String memberRegister(RequestContext rc, String url, RegisterReq req, String verifyCode) {

        //校验用户名是否包含非法字符
        if (UserUtil.checkoutUserName(req.getUsername())) {
            throw UserException.ILLEGAL_USERNAME;
        }

        //验证码校验
        verifyCode(rc, verifyCode);
        OwnerInfo ownerInfo = dubboOutAssembleService.getOwnerInfoByDomain(url);

        if (ownerInfo == null || ownerInfo.getOwnerId() < 0) {
            throw UserException.ILLEGAL_SOURCE_URL;
        }

        if (!checkRegisterParam(req, ownerInfo.getOwnerId(), AccountType.member.value())) {
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
            if (agentUser != null && agentUser.getOwnerId().equals(ownerInfo.getOwnerId())) {
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

        String body = assembleRegisterBody(rc, url, ownerInfo.getOwnerId(), agent.getUserId(), req);
        ApiLogger.info(body);
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
        String token = null;
        try {
            JSONObject object = JSONObject.parseObject(resp.getData());
            userId = object.getLongValue("uid");
            token = object.getString("token");
        } catch (Exception e) {
            ApiLogger.error(String.format("passport register return data error. resp: %s", JSON.toJSONString(resp)), e);
        }
        if (userId <= 0 || StringUtils.isEmpty(token)) {
            throw UserException.REGISTER_FAIL;
        }
        Member member = assembleMember(rc, req, userId, ownerInfo.getOwnerId(), ownerInfo.getOwnerName(), holder, agent, url);
        boolean result = memberService.saveMember(member);
        if (!result) {
            throw UserException.REGISTER_FAIL;
        }
        sendRegisterMessage(member);
        return "{\"token\":" + "\"" + token + "\"" + "}";
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
        member.setUsername(req.getUsername());
        member.setRealname(req.getRealname());
        member.setTelephone(req.getTelephone());
        member.setEmail(req.getEmail());
        member.setBank(req.getBank());
        member.setBankCode(req.getBankCode());
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
        object.put("deviceId", rc.getClient().getDeviceId());
        return object.toJSONString();
    }

    /**
     * 检查注册请求数据合法性
     *
     * @param req
     * @return
     */
    private boolean checkRegisterParam(RegisterReq req, Long ownerId, int type) {
        //校验用户名和密码
        if (!Optional.ofNullable(req)
                .filter(request -> request.getUsername() != null && request.getUsername().length() >= 4
                        && request.getUsername().length() <= 15)
                .filter(request -> request.getPassword() != null && request.getPassword().length() == 32)
                .isPresent()) {
            return false;
        }
        //校验其他注册参数
        List<String> list = dubboOutAssembleService.getMustRegisterarameters(ownerId, type);
        if (list != null && list.size() > 0) {
            for (String name : list) {
                try {
                    Field field = req.getClass().getField(name);
                    field.setAccessible(true);
                    if (field.get(req) == null) {
                        return false;
                    }
                    if ("paymentPassword".equals(name)) {
                        if (((String) field.get(req)).length() != 32) {
                            return false;
                        }
                    }
                } catch (NoSuchFieldException e) {

                } catch (IllegalAccessException e) {
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
     * @param code     验证码
     * @return
     */
    public String memberLogin(RequestContext rc, String agent, String url, String username, String password, String code) {
        if (!checkLoginReq(username, password)) {
            throw UserException.ILLEDGE_USERNAME_PASSWORD;
        }
        verifyCode(rc, code);
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
        if (!Optional.ofNullable(member).isPresent()) {
            throw UserException.ILLEGAL_USER;
        }
        if (member.getStatus() == AccountStatus.disable) {
            throw UserException.MEMBER_ACCOUNT_DISABLED;
        }
        //异步回收资金
//        thriftOutAssembleService.backMoney(uid, 2);
        String token = object.getString("token");
        String result = assembleLoginResult(uid, member.getUsername(), token);
        sendLoginMessage(member, rc);

        return result;
    }

    /**
     * 验证码检测
     *
     * @param rc
     * @param code
     */
    private void verifyCode(RequestContext rc, String code) {
        if (StringUtils.isEmpty(code)) {
            throw UserException.VERIFY_CODE_ERROR;
        }
        String clientId = getClientId(rc);
        if (StringUtils.isEmpty(clientId)) {
            throw UserException.VERIFY_CODE_INVALID;
        }
        String verifyCodeAndExpireTime = memberService.getVerifyCode(clientId);
        //验证码验证
        checkVerifyCode(verifyCodeAndExpireTime, code);
    }

    /**
     * 会员登陆
     *
     * @param rc
     * @param agent
     * @param url
     * @param username
     * @param password
     * @return
     */
    public String memberLogin(RequestContext rc, String agent, String url, String username, String password) {
        if (!checkLoginReq(username, password)) {
            throw UserException.ILLEDGE_USERNAME_PASSWORD;
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
        if (!Optional.ofNullable(member).isPresent()) {
            throw UserException.ILLEGAL_USER;
        }
        if (member.getStatus() == AccountStatus.disable) {
            throw UserException.MEMBER_ACCOUNT_DISABLED;
        }

        String token = object.getString("token");
        String result = assembleLoginResult(uid, member.getUsername(), token);
        sendLoginMessage(member, rc);

        return result;
    }

    /**
     * 验证码校验
     *
     * @param verifyCodeAndExpireTime
     * @param code
     */
    private void checkVerifyCode(String verifyCodeAndExpireTime, String code) {
        //TODO 待删除
        if (StringUtils.isNoneEmpty(code) && code.equals("000000")) {
            return;
        }
        if (StringUtils.isEmpty(verifyCodeAndExpireTime)) {
            throw UserException.VERIFY_CODE_INVALID;
        }
        String[] split = verifyCodeAndExpireTime.split(UserContants.SPLIT_LINE);
        if (split == null || split.length != 2) {
            throw UserException.VERIFY_CODE_INVALID;
        }
        String verifyCode = split[0];
        long time;
        try {
            time = Long.parseLong(split[1]);
        } catch (Exception e) {
            ApiLogger.error(String.format("parse verifycode of time error. msg: %s", e.getMessage()));
            throw UserException.VERIFY_CODE_INVALID;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis > time) {
            ApiLogger.info(String.format("verify code expire. verifyCode: %s, code: %s, ctime: %d", verifyCodeAndExpireTime, code, currentTimeMillis));
            throw UserException.VERIFY_CODE_INVALID;
        }
        if (StringUtils.isEmpty(verifyCode)) {
            ApiLogger.info(String.format("verify code empty. verifyCode: %s, code: %s, ctime: %d", verifyCodeAndExpireTime, code, currentTimeMillis));
            throw UserException.VERIFY_CODE_INVALID;
        }
        if (!verifyCode.toUpperCase().equals(code.toUpperCase())) {
            throw UserException.VERIFY_CODE_ERROR;
        }
    }

    /**
     * 组装返回数据
     *
     * @param uid
     * @param username
     * @param token
     * @return
     */
    private String assembleLoginResult(long uid, String username, String token) {
        LoginSuccessInfoVo loginInfo = new LoginSuccessInfoVo();
        loginInfo.setId(uid);
        loginInfo.setUsername(username);
        loginInfo.setToken(token);
        long message = dubboOutAssembleService.getNoReadMessageCount(uid);
        loginInfo.setMessage(message);
        String balance = thriftOutAssembleService.getMemberBalance(uid);
        loginInfo.setBalance(balance);
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
        object.put("deviceId", rc.getClient().getDeviceId());
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
        if (username == null || username.length() < 4 || username.length() > 15) {
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
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return
     */
    public String memberPasswordReset(RequestContext rc, String oldPassword, String newPassword) {
        if (!checkResetParams(oldPassword, newPassword)) {
            throw UserException.PASSWORD_RESET_FAIL;
        }
        Member member = memberService.getMemberById(rc.getUid());
        if (!Optional.ofNullable(member).isPresent()) {
            throw UserException.ILLEGAL_MEMBER;
        }
        String body = assembleResetBody(rc, member.getUsername(), oldPassword, newPassword);
        EGResp resp = thriftOutAssembleService.memberPasswordReset(body, "account");
        if (resp == null) {
            throw UserException.PASSWORD_RESET_FAIL;
        }
        int code = resp.getCode();
        if (code == 0x1009) {
            throw UserException.OLD_PASSWORD_ERROR;
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
     * @param oldPassword
     * @param newPassword
     * @return
     */
    private boolean checkResetParams(String oldPassword, String newPassword) {
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
        if (list != null && list.size() > 0) {
            for (OnLineMember member : list) {
				member.setCity("未知城市");
                if(null != member.getLoginIp() && !"".equals(member.getLoginIp())){
                    member.setCity(getAddressByIP(member.getLoginIp(),this.URL));
                }
            }
        }
        return JSON.toJSONString(assemblePage(page, count, total, assembleOnlineMemberVo(list)));
    }

    /**
     * 在线会员列表导出
     *
     * @param rc
     * @param condition
     * @return
     */
    public DownLoadFile onlineListExport(RequestContext rc, String condition
                                         /*Long loginStartTime,Long loginEndTime,Long registerStartTime,Long registerEndTime*/) {
        long uid = rc.getUid();
        User user = userService.getUserById(uid);
        if (user == null) {
            throw UserException.ILLEGAL_USER;
        }

        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            throw UserException.ILLEGAL_USER;
        }
        String filename = ExcelUtil.assembleFileName(operaUser.getUserId(), ExcelUtil.ONLINE_MEMBER_LIST);
        DownLoadFile downLoadFile = new DownLoadFile();
        downLoadFile.setFilename(filename);
        byte[] content = new byte[0];

        OnlineMemberConditon memberCondition = parseContion(condition, user);
//        memberCondition.setLoginStartTime(loginStartTime);
//        memberCondition.setLoginEndTime(loginEndTime);
//        memberCondition.setRegisterStartTime(registerStartTime);
//        memberCondition.setRegisterEndTime(registerEndTime);
        List<OnLineMember> list = memberMongoService.getOnlineMembers(memberCondition, null, null);
        List<OnLineMemberVo> members = (List<OnLineMemberVo>) assembleOnlineMemberVo(list);
        //查询表数据，生成excel的zip，并返回zip byte[]
        content = ExcelUtil.onLineMemberListExport(members, filename);
        downLoadFile.setContent(content);
        return downLoadFile;
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
                    onLineMemberVo.setLoginTime(LocalDateTimeUtil.toAmerica(next.getLoginTime()));
                }
                onLineMemberVo.setRegisterTime(LocalDateTimeUtil.toAmerica(next.getRegisterTime()));
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
     * 获取会员的银行卡信息
     *
     * @param rc
     * @return
     */
    public String bankDetail(RequestContext rc) {
        Member member = memberService.getMemberById(rc.getUid());
        if (member == null) {
            throw UserException.ILLEGAL_USER;
        }
        BankDetailVo result = new BankDetailVo();
        result.setBank(member.getBank());
        result.setBankCardNo(member.getBankCardNo());
        result.setRealname(member.getRealname());
        result.setTelephone(member.getTelephone());
        result.setBankCode(member.getBankCode());
        result.setBankDetail(member.getBankDeposit());
        if (member.getBankCardNo() == null || member.getBankCardNo().trim().length() == 0 ||
                member.getBank() == null || member.getBank().trim().length() == 0) {
            result.setHave(false);
        } else {
            result.setHave(true);
        }

        return JSONObject.toJSONString(result);
    }

    /**
     * 查询个人中心详细信息
     *
     * @param rc
     * @return
     */
    public String memberCenterDetail(RequestContext rc) {
        Member member = memberService.getMemberById(rc.getUid());
        if (member == null) {
            throw UserException.ILLEGAL_USER;
        }

        MemberCenterDetailVo memberCenterDetailVo = new MemberCenterDetailVo();
        SubAccount subAccount = dubboOutAssembleService.getSubLoginById(member.getMemberId());
        if (subAccount != null && subAccount.getLastTime() != 0) {
            memberCenterDetailVo.setLastLoginTime(LocalDateTimeUtil.toAmerica(subAccount.getLastTime()));
        }

        initMemberCenterDetailVo(memberCenterDetailVo, member);

        return JSONObject.toJSONString(memberCenterDetailVo);
    }

    private void initMemberCenterDetailVo(MemberCenterDetailVo o, Member member) {
        if (o == null || member == null) {
            return;
        }
        if (StringUtils.isNotEmpty(member.getBankCardNo())) {
            o.setBankCardNo(member.getBankCardNo());
        } else {
            o.setBankCardNo("尚未设置提款银行卡");
        }
        if (StringUtils.isNotEmpty(member.getWeixin())) {
            o.setWeixin(member.getWeixin());
        } else {
            o.setWeixin("无");
        }
        if (StringUtils.isNotEmpty(member.getQq())) {
            o.setQq(member.getQq());
        } else {
            o.setQq("无");
        }
        if (StringUtils.isNotEmpty(member.getUsername())) {
            o.setUsername(member.getUsername());
        } else {
            o.setUsername("无");
        }
        if (StringUtils.isNotEmpty(member.getRealname())) {
            o.setRealname(member.getRealname());
        } else {
            o.setRealname("无");
        }
        if (StringUtils.isNotEmpty(member.getEmail())) {
            o.setEmail(member.getEmail());
        } else {
            o.setEmail("无");
        }
        if (StringUtils.isNotEmpty(member.getBank())) {
            o.setBank(member.getBank());
        }
        if (StringUtils.isNotEmpty(member.getBankCode())) {
            o.setBankCode(member.getBankCode());
        }
        if (StringUtils.isNotEmpty(member.getTelephone())) {
            o.setTelephone(member.getTelephone());
        } else {
            o.setTelephone("无");
        }
        if (StringUtils.isNotEmpty(member.getBankDeposit())) {
            o.setBankDeposit(member.getBankDeposit());
        } else {
            o.setBankDeposit("无");
        }
    }

    /**
     * 获取会员的余额和未读消息
     *
     * @param rc
     * @return
     */
    public String getMemberInfo(RequestContext rc) {
        long message = dubboOutAssembleService.getNoReadMessageCount(rc.getUid());
        //同步回收
        thriftOutAssembleService.backMoney(rc.getUid(), 1);
        String balance = thriftOutAssembleService.getMemberBalance(rc.getUid());
        return "{\"message\":" + message + ",\"balance\":\"" + balance + "\"}";
    }

    /**
     * 返回验证码
     *
     * @param rc
     * @param code
     * @return
     */
    public String saveCode(RequestContext rc, String code) {
        String clientId = rc.getRequest().getSession().getId();
        if (StringUtils.isEmpty(clientId)) {
            clientId = UUIDUtil.getUUID();
        }
        ApiLogger.info(String.format("refresh code. clientId: %s, code: %s", clientId, code));
        boolean result = memberService.refreshCode(clientId, code);
        if (!result) {
            throw UserException.GET_VERIFY_CODE_ERROR;
        }
        return clientId;
    }

    /**
     * 获取客户端ID
     *
     * @param rc
     * @return
     */
    private String getClientId(RequestContext rc) {
        try {
            return rc.getRequest().getHeader(UserContants.X_VERIFY_CODE);
        } catch (Exception e) {
            ApiLogger.error("get verify code from header error.", e);
        }
        return null;
    }

    /**
     * 会员资金概况
     *
     * @param rc
     * @param memberId
     * @return
     */
    public String fundProfileRefresh(RequestContext rc, Long memberId) {
        if (!Optional.ofNullable(memberId).filter(value -> value > 0).isPresent()) {
            throw UserException.ILLEGAL_MEMBER;
        }
        /**
         * 会员资金概况
         */
        MemberFundInfo memberFundInfoObj;
        FundProfile fundProfile = new FundProfile();
        String capitalBody = "{\"memberId\":" + memberId + "}";
        EGResp capitalResp = thriftOutAssembleService.getMemberCapital(capitalBody, "account");
        if (capitalResp != null && capitalResp.getData() != null) {
            JSONObject capitalData = JSONObject.parseObject(capitalResp.getData());
            fundProfile.setSyncTime(capitalData.getString("syncTime"));
            memberFundInfoObj = new MemberFundInfo();
            memberFundInfoObj.setBalance(capitalData.getString("balance"));
            memberFundInfoObj.setLastDeposit(capitalData.getString("lastDeposit"));
            memberFundInfoObj.setLastWithdraw(capitalData.getString("lastWithdraw"));
        } else {
            fundProfile.setSyncTime(new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").format(new Date()));
            memberFundInfoObj = new MemberFundInfo();
            memberFundInfoObj.setBalance("0");
            memberFundInfoObj.setLastDeposit("0");
            memberFundInfoObj.setLastWithdraw("0");

        }
        fundProfile.setInfo(memberFundInfoObj);
        return JSON.toJSONString(fundProfile);
    }

    /**
     * 获取会员的交易记录
     *
     * @param rc
     * @param memberId
     * @return
     */
    public String memberTradingRecord(RequestContext rc, Long memberId) {
        ApiLogger.info("memberId:" + memberId);
        MemberConditionVo mv = memberMongoService.get(memberId);
        MemberConditionWebVo result = new MemberConditionWebVo();
        if (mv == null) {
            result.setMemberId(memberId);
            result.setDepositCount(0);
            result.setDepositMoney("0");
            result.setLastDepositMoney("0");
            result.setMaxDepositMoney("0");

            result.setWithdrawCount(0);
            result.setWithdrawMoney("0");
            result.setLastDepositMoney("0");
            result.setMaxWithdrawMoney("0");
        } else {
            result.setDepositCount(mv.getDepositCount() == null ? 0 : mv.getDepositCount());
            result.setDepositMoney(mv.getDepositMoney() == null ? "" : NumberUtil.fenToYuan(mv.getDepositMoney()).toString());
            result.setLastDepositMoney(mv.getLastDepositMoney() == null ? "0" : NumberUtil.fenToYuan(mv.getLastDepositMoney()).toString());
            result.setMaxDepositMoney(mv.getMaxDepositMoney() == null ? "0" : NumberUtil.fenToYuan(mv.getMaxDepositMoney()).toString());

            result.setWithdrawCount(mv.getWithdrawCount() == null ? 0 : mv.getWithdrawCount());
            result.setWithdrawMoney(mv.getWithdrawMoney() == null ? "0" : NumberUtil.fenToYuan(mv.getWithdrawMoney()).toString());
            result.setLastWithdrawMoney(mv.getLastWithdrawMoney() == null ? "0" : NumberUtil.fenToYuan(mv.getLastWithdrawMoney()).toString());
            result.setMaxWithdrawMoney(mv.getMaxWithdrawMoney() == null ? "0" : NumberUtil.fenToYuan(mv.getMaxWithdrawMoney()).toString());
        }
        return JSON.toJSONString(result);
    }

    /**
     * 客端添加银行卡信息
     *
     * @param rc
     * @param realname
     * @param telephone
     * @param bankCode
     * @param bank
     * @param bankCardNo
     * @return
     */
    public String addBankInfo(RequestContext rc, String realname, String telephone, String bankCode,
                              String bank, String bankCardNo, String bankAddress) {
        Member member = memberService.getMemberById(rc.getUid());
        if (member == null) {
            throw UserException.ILLEGAL_USER;
        }
        if (!checkBankInfo(realname, telephone, bankCode, bank, bankCardNo, bankAddress)) {
            throw UserException.ILLEGAL_PARAMETERS;
        }

        assembleBankInfo(member, realname.trim(), telephone.trim(), bankCode.trim(), bank.trim(), bankCardNo.trim(), bankAddress.trim());

        memberService.updateMember(member);
        return JSON.toJSONString(member);
    }

    /**
     * 组装会员银行卡信息
     *
     * @param member
     * @param realname
     * @param telephone
     * @param bankCode
     * @param bank
     * @param bankCardNo
     */
    private void assembleBankInfo(Member member, String realname, String telephone,
                                  String bankCode, String bank, String bankCardNo, String bankAddress) {
        if (member == null) {
            return;
        }
        if (!(member.getRealname() != null && member.getRealname().trim().length() > 0)) {
            member.setRealname(realname);
        }
        member.setTelephone(telephone);
        member.setBank(bank);
        member.setBankCode(bankCode);
        member.setBankCardNo(bankCardNo);
        member.setBankDeposit(bankAddress);
    }

    /**
     * 校验添加会员银行卡信息参数
     *
     * @param realname
     * @param telephone
     * @param bankCode
     * @param bank
     * @param bankCardNo
     * @return
     */
    private boolean checkBankInfo(String realname, String telephone, String bankCode, String bank, String bankCardNo, String bankAddress) {
        if (realname.trim().length() < 1) {
            return false;
        }
        if (telephone.trim().length() != 11) {
            return false;
        }
        if (bankCardNo.trim().length() < 15) {
            return false;
        }
        if (bankAddress.trim().length() < 1) {
            return false;
        }
        return true;
    }

    /**
     * 检测用户名
     *
     * @param rc
     * @param username
     * @return
     */
    public String usernameCheck(RequestContext rc, String username) {
        //根据url获取业主ID
        OwnerInfo ownerInfo = dubboOutAssembleService.getOwnerInfoByDomain(rc.getOrigin());
        if (ownerInfo == null || ownerInfo.getOwnerId() < 0) {
            throw UserException.ILLEGAL_SOURCE_URL;
        }
        long uid = dubboOutAssembleService.getUid(ownerInfo.getOwnerId(), username);
        if (uid > 0) {
            throw UserException.USERNAME_EXIST;
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * 获取会员当前余额
     *
     * @param rc
     * @return
     */
    public String getBalance(RequestContext rc) {
        String balance = thriftOutAssembleService.getMemberBalance(rc.getUid());
        return "{\"balance\":\"" + balance + "\"}";
    }

    /**
     * 获取满足分层条件会员数
     *
     * @param rc
     * @param condition
     * @return
     */
    public String memberListCount(RequestContext rc, String condition) {
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            return JSON.toJSONString(assemblePageBeanMemberCondions(null, null, 0L, null));
        }
        MemberCondition memberCondition = MemberCondition.valueOf(condition);
        if (memberCondition == null) {
            memberCondition = new MemberCondition();
        }
        memberCondition.setOwnerId(operaUser.getOwnerId());
        if (!checkCondition(memberCondition)) {
            return JSON.toJSONString(assemblePageBeanMemberCondions(null, null, 0L, null));
        }
        long total = memberMongoService.getCount(memberCondition);
        if (total <= 0) {
            return JSON.toJSONString(assemblePageBeanMemberCondions(null, null, 0L, null));
        }
        //获取mongo中查询到的会员列表
        List<MemberConditionVo> memberConditionVos = memberMongoService.queryByPage(memberCondition, null, null);
        ApiLogger.info(String.format("get member conditon from mongo. members: %s", JSON.toJSONString(memberConditionVos)));
        if (!Optional.ofNullable(memberConditionVos).filter(size -> size.size() > 0).isPresent()) {
            return JSON.toJSONString(assemblePageBeanMemberCondions(null, null, 0L, null));
        }
        return JSON.toJSONString(assemblePageBeanMemberCondions(null, null, total, memberConditionVos));
    }

    /**
     * 获取满足分层条件
     *
     * @param rc
     * @param condition
     * @return
     */
    public String memberListSearch(RequestContext rc, String condition) {
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            return JSON.toJSONString(assemblePageBean(null, null, null, null));
        }
        MemberCondition memberCondition = MemberCondition.valueOf(condition);
        if (memberCondition == null) {
            memberCondition = new MemberCondition();
        }
        memberCondition.setOwnerId(operaUser.getOwnerId());
        if (!checkCondition(memberCondition)) {
            return JSON.toJSONString(assemblePageBean(null, null, null, null));
        }
        //获取mongo中查询到的会员列表
        List<MemberConditionVo> memberConditionVos = memberMongoService.queryByPage(memberCondition, null, null);
        ApiLogger.info(String.format("get member conditon from mongo. members: %s", JSON.toJSONString(memberConditionVos)));
        if (!Optional.ofNullable(memberConditionVos).filter(size -> size.size() > 0).isPresent()) {
            return JSON.toJSONString(assemblePageBean(null, null, null, null));
        }
        return JSON.toJSONString(assemblePageBeanMemberCondions(null, null, null, memberConditionVos));
    }

    /**
     * 检索会员列表
     *
     * @param rc
     * @param accounts
     * @return
     */
    public String memberListSearchByAccounts(RequestContext rc, String accounts) {
        if (!checkParams(accounts)) {
            return JSON.toJSONString(assemblePageBean(null, null, null, null));
        }
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            return JSON.toJSONString(assemblePageBean(null, null, null, null));
        }
        Set<String> sets = Arrays.asList(accounts.split(",")).stream().map(s -> s.trim()).collect(Collectors.toSet());
        //获取mongo中查询到的会员列表
        List<MemberConditionVo> memberConditionVos = memberMongoService.batchQuery(sets, operaUser.getOwnerId());
        ApiLogger.info(String.format("get member conditon from mongo. members: %s", JSON.toJSONString(memberConditionVos)));
        if (!Optional.ofNullable(memberConditionVos).filter(size -> size.size() > 0).isPresent()) {
            return JSON.toJSONString(assemblePageBean(null, null, null, null));
        }
        return JSON.toJSONString(assemblePageBeanMemberCondions(null, null, null, memberConditionVos));
    }

    /**
     * 参数检查
     *
     * @param accounts
     * @return
     */
    private boolean checkParams(String accounts) {
        if (StringUtils.isNotEmpty(accounts)) {
            try {
                String[] split = accounts.split(",");
                if (split.length == 0) {
                    return false;
                }
                return true;
            } catch (Exception e) {
                ApiLogger.error(String.format("spilt accounts error. accounts: %s", accounts), e);
            }
        }
        return false;
    }
    /**
     * 根据ip获取城市名
     * @param ip
     * @param URL
     * @return
     */
    public static String getAddressByIP(String ip, String URL) {
        try {
            java.net.URL url = new URL(URL + ip);
            URLConnection conn = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "GBK"));
            String line = null;
            StringBuffer result = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
            JSONObject object = JSONObject.parseObject(result.toString());
            if (object.size() > 0) {
                if(object.getString("city") != null && !object.getString("city").equals("")){
                    return object.getString("city");
                }
                if(object.getString("province") != null && !object.getString("province").equals("")){
                    return object.getString("province");
                }
            }
        } catch (IOException e) {
            return "读取失败";
        }
        return "未知城市";
    }

}
