package com.magic.user.member.resource.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.model.Page;
import com.magic.api.commons.model.PageBean;
import com.magic.api.commons.model.SimpleListResult;
import com.magic.api.commons.model.SimpleResult;
import com.magic.commons.enginegw.EngineUtil;
import com.magic.config.thrift.base.CmdType;
import com.magic.config.thrift.base.EGHeader;
import com.magic.config.thrift.base.EGReq;
import com.magic.config.thrift.base.EGResp;
import com.magic.user.bean.Account;
import com.magic.user.bean.MemberCondition;
import com.magic.user.bean.RegionNumber;
import com.magic.user.bean.Register;
import com.magic.user.constants.UserContants;
import com.magic.user.entity.Member;
import com.magic.user.entity.User;
import com.magic.user.enums.AccountStatus;
import com.magic.user.enums.AccountType;
import com.magic.user.enums.CurrencyType;
import com.magic.user.exception.UserException;
import com.magic.user.po.DownLoadFile;
import com.magic.user.service.MemberService;
import com.magic.user.vo.MemberDetailVo;
import com.magic.user.vo.MemberLevelListVo;
import com.magic.user.vo.MemberListVo;
import com.magic.user.vo.UserCondition;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
        MemberCondition memberCondition = MemberCondition.valueOf(condition);
        if (!checkCondition(memberCondition)){
            return JSON.toJSONString(assemblePageBean(page, count, 0, null));
        }
        long uid = rc.getUid(); //业主ID、股东或代理ID
        //TODO mongo 检索满足条件的数据记录数
        long total = 0;
        if (total <= 0){
            return JSON.toJSONString(assemblePageBean(page, count, 0, null));
        }
        //TODO mongo 检索满足条件的数据列表
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
     * @param condition
     * @return
     */
    private boolean checkCondition(MemberCondition condition) {
        Integer currencyType = condition.getCurrencyType();
        if (currencyType != null && CurrencyType.parse(currencyType) == null){
            return false;
        }
        Account account = condition.getAccount();
        if (account != null && StringUtils.isNoneEmpty(account.getName())){
            Integer type = account.getType();
            if (type == null || AccountType.parse(type) == null) {
                return false;
            }
        }
        RegionNumber region = condition.getDepositNumber();
        if (region != null && region.getMin() != null && region.getMax() != null && region.getMin() > region.getMax()){
            return false;
        }
        Register register = condition.getRegister();
        if (register != null && register.getStart() != null && register.getEnd() != null && register.getStart() > register.getEnd()){
            return false;
        }
        region = condition.getWithdrawNumber();
        if (region != null && region.getMin() != null && region.getMax() != null && region.getMin() > region.getMax()){
            return false;
        }
        Integer status = condition.getStatus();
        if (status != null && AccountStatus.parse(status) == null){
            return false;
        }
        return true;
    }

    /**
     * 组装翻页数据
     *
     * @param page 页码
     * @param count 当页条数
     * @param total 总条数
     * @param list 详细列表数据
     * @return
     */
    private static PageBean<MemberListVo> assemblePageBean(int page, int count, long total, Collection<MemberListVo> list){
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
     * @param rc RequestContext
     * @param condition 检索条件
     * @return
     */
    public DownLoadFile memberListExport(RequestContext rc, String condition) {
        MemberCondition memberCondition = MemberCondition.valueOf(condition);
        if (!checkCondition(memberCondition)){
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
        if (member == null){
            throw UserException.ILLEGAL_MEMBER;
        }
        //TODO 1.jason 根据会员ID查询会员优惠方案
        //TODO 2.jason 根据会员ID查询会员资金概括
        //TODO 3.jason 根据会员ID查询投注记录
        //TODO 4.jason 根据会员ID查询优惠记录
        MemberDetailVo detail = assembleMemberDetail();
        return JSON.toJSONString(detail);
    }

    /**
     * 组装会员详情 //TODO
     * @return
     */
    private MemberDetailVo assembleMemberDetail() {
        return null;
    }

    /**
     * 密码重置 -- 后台
     * @param rc
     * @param id
     * @param password
     * @return
     */
    public String passwordReset(RequestContext rc, long id, String password) {
        Member member = memberService.getMemberById(id);
        if (member == null){
            throw UserException.ILLEGAL_MEMBER;
        }
        String username = member.getAgentUsername();
        //组装请求数据
        JSONObject body = new JSONObject();
        body.put("username", username);
        body.put("password", password);
        //TODO cmd 确认
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x00001, body.toJSONString());
        EGResp resp = EngineUtil.call(req, "account");
        if (resp != null && resp.getCode() == 0x11111){//重置成功
            return UserContants.EMPTY_STRING;
        }
        throw UserException.PASSWORD_RESET_FAIL;
    }

    /**
     * 组装请求对象
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
        if (member == null){
            throw UserException.ILLEGAL_MEMBER;
        }
        //组装请求数据
        //TODO cmd 确认
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x00001, logoutBody(rc, member));
        EGResp resp = EngineUtil.call(req, "account");
        if (resp != null && resp.getCode() == 0x11111){//重置成功
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
        body.put("operatorTime", System.currentTimeMillis() / 1000);
        return body.toJSONString();
    }

    /**
     * 更新会员基础信息
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
        if (!result){
            throw UserException.MEMBER_UPDATE_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * 组装会员数据
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
        member.setId(id);
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
    public String updateLevel(RequestContext rc, long id, int level) {
        Member member = memberService.getMemberById(id);
        if (member == null){
            throw UserException.ILLEGAL_MEMBER;
        }
        //TODO andy dubbo
        boolean result = false;
        if (!result){
            throw UserException.MEMBER_LEVEL_UPDATE_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * 会员层级列表
     *
     * @param rc RequestContext
     * @param lock 是否锁定 1：非锁定 2锁定
     * @param page 页码
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
     * @param rc RequestContext
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
     * @param rc RequestContext
     * @param id 会员ID
     * @param status 1 启用 2禁用
     * @return
     */
    public String memberStatusUpdate(RequestContext rc, Long id, Integer status) {
        if (!checkParams(id, status)){
            throw UserException.ILLEGAL_PARAMETERS;
        }
        AccountStatus newStatus = AccountStatus.parse(status);
        AccountStatus oldStatus = AccountStatus.enable;
        if (newStatus == AccountStatus.enable){
            oldStatus = AccountStatus.disable;
        }
        boolean result = memberService.updateStatus(id, oldStatus, newStatus);
        if (!result){
            throw UserException.MEMBER_STATUS_UPDATE_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * 参数检查
     * @param id
     * @param status
     * @return
     */
    private boolean checkParams(Long id, Integer status) {
        if (id == null || id <= 0){
            return false;
        }
        if (status == null){
            return false;
        }
        AccountStatus accountStatus = AccountStatus.parse(status);
        if (accountStatus != AccountStatus.disable || accountStatus != AccountStatus.enable){
            return false;
        }
        return true;
    }

    public Page<User> findByPage(UserCondition userCondition) {
        return memberService.findByPage(userCondition);
    }
}
