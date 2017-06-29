package com.magic.user.service.thrift;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.tools.NumberUtil;
import com.magic.api.commons.utils.StringUtils;
import com.magic.commons.enginegw.service.ThriftFactory;
import com.magic.config.thrift.base.CmdType;
import com.magic.config.thrift.base.EGHeader;
import com.magic.config.thrift.base.EGReq;
import com.magic.config.thrift.base.EGResp;
import com.magic.user.constants.UserContants;
import com.magic.user.entity.Member;
import com.magic.user.vo.AgentConfigVo;
import com.magic.user.vo.MemberPreferScheme;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/22
 * Time: 17:32
 * @Doc 统一管理对外的thrift调用
 */
@Service("thriftOutAssembleService")
public class ThriftOutAssembleServiceImpl {

    @Resource
    private ThriftFactory thriftFactory;

    /**
     * 会员注册
     * @param body
     * @param caller
     * @return
     */
    public EGResp memberRegister(String body, String caller) {
        EGReq egReq = assembleEGReq(CmdType.PASSPORT, 0x100001, body);
        return thriftFactory.call(egReq, caller);
    }

    /**
     * 会员登录
     * @param body
     * @param caller
     * @return
     */
    public EGResp memberLogin(String body, String caller) {
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100002, body);
        return thriftFactory.call(req, caller);
    }

    /**
     * 会员密码重置
     * @param body
     * @param caller
     * @return
     */
    public EGResp memberPasswordReset(String body, String caller) {
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100004, body);
        return thriftFactory.call(req, caller);
    }

    /**
     * @doc 登陆状态检测
     * @param body
     * @param caller
     * @return
     */
    public EGResp memberLoginVerify(String body, String caller) {
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100003, body);
        return thriftFactory.call(req, caller);
    }

    /**
     * @Doc 登陆注销
     * @param body
     * @param caller
     * @return
     */
    public EGResp memberLogout(String body, String caller) {
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100005, body);
        return thriftFactory.call(req, caller);
    }

    /**
     * 密码重置 -- 后台
     * @param body
     * @param caller
     * @return
     */
    public EGResp passwordReset(String body, String caller) {
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100006, body);
        return thriftFactory.call(req, caller);
    }

    /**
     * @Doc 会员强制下线
     * @param body
     * @param caller
     * @return
     */
    public EGResp logout(String body, String caller) {
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100005, body);
        return thriftFactory.call(req, caller);
    }

    /**调用kevin Thrift接口**/
    /**
     * 获取单个会员资金概况
     * @param body
     * @param caller
     * @return
     */
    public EGResp getMemberCapital(String body, String caller) {
        EGReq req = assembleEGReq(CmdType.SETTLE, 0x300001, body);
        return thriftFactory.call(req, caller);
    }

    /**
     * 获取会员余额
     * @param uid
     *
     * @return
     */
    public String getMemberBalance(long uid) {
        String body = "{\"UserId\":" + uid + ",\"Flag\":" + 1 + "}";
        EGReq req = assembleEGReq(CmdType.SETTLE, 0x300001, body);
        String balance = "0";
        try {
            EGResp call = thriftFactory.call(req, UserContants.CALLER);
            if (Optional.ofNullable(call).filter(code -> code.getCode() == 0).isPresent()){
                JSONObject data = JSONObject.parseObject(call.getData());
                long value = data.getLongValue("Balance");
                balance = String.valueOf(NumberUtil.fenToYuan(value));
            }
        }catch (Exception e){
            ApiLogger.error(String.format("get user balance error. uid: %d", uid), e);
        }
        if (StringUtils.isEmpty(balance)){
            balance = "0";
        }
        backMoney(uid);//回收资金
        return balance;
    }

    /**
     * 获取会员的余额列表
     *
     * @param ids
     * @return
     */
    public Map<Long, String> getMemberBalances(Collection<Long> ids) {
        Map<Long, String> result = new HashMap<>();
        JSONObject body = new JSONObject();
        body.put("UserIds", ids);
        try {
            EGResp resp = thriftFactory.call(CmdType.SETTLE, 0x300015, body.toJSONString(), UserContants.CALLER);
            if (!Optional.ofNullable(resp).filter(data -> data.getData() != null).isPresent()){
                return result;
            }
            JSONArray array = JSONObject.parseArray(resp.getData());
            if (!Optional.ofNullable(array).filter(size -> size.size() > 0).isPresent()) {
                return result;
            }
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                try {
                    String balance = object.getLong("Balance") == null ? "0":
                            String.valueOf(NumberUtil.fenToYuan(object.getLong("Balance")));
                    result.put(object.getLong("UserId"), balance);
                }catch (Exception e){
                    ApiLogger.error(String.format("parse object to balance error. object: %s", JSON.toJSONString(object)));
                }
            }
        }catch (Exception e){
            ApiLogger.error(String.format("get member balances error. ids: %s", JSON.toJSONString(ids)), e);
        }
        return result;
    }

    /**
     * 重置会员的支付密码
     * @param body
     * @param caller
     * @return
     */
    public EGResp resetMemberPayPwd(String body, String caller) {
        //TODO 修改cmdType 和 cmd值
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100005, body);
        return thriftFactory.call(req, caller);
    }

    /**调用Jason Thrift接口**/
    /**
     * 获取代理参数配置信息
     * @param uid 代理id
     * @return
     */
    public AgentConfigVo getAgentConfig(long uid) {
        String body = "{\"agentId\":" + uid + "}";
        EGReq req = assembleEGReq(CmdType.CONFIG, 0x500043, body);
        try {
            EGResp resp = thriftFactory.call(req, UserContants.CALLER);
            if (Optional.ofNullable(resp).filter(code -> code.getCode() == 0).isPresent()){
                return JSONObject.parseObject(resp.getData(), AgentConfigVo.class);
            }
        }catch (Exception e){
            ApiLogger.error(String.format("get agent config from thrift error. uid: %d", uid), e);
        }
        return null;
    }

    /**
     * 查询会员层级对应的优惠方案
     *
     * @param level
     * @return
     */
    public MemberPreferScheme getMemberPrivilege(Long level) {
        String body = "{\"level\":" + level + "}";
        try {
            EGReq req = assembleEGReq(CmdType.CONFIG, 0x500040, body);
            EGResp resp = thriftFactory.call(req, UserContants.CALLER);
            if (Optional.ofNullable(resp).filter(code -> code.getCode() == 0).isPresent()){
                return assembleMemberPreferScheme(resp.getData());
            }
        }catch (Exception e){
            ApiLogger.error(String.format("get member privilege error."));
        }
        return null;
    }

    /**
     * 组装数据
     * @param data
     * @return
     */
    private MemberPreferScheme assembleMemberPreferScheme(String data) {
        MemberPreferScheme scheme = new MemberPreferScheme();
        try {
            JSONObject objectData = JSONObject.parseObject(data);
            JSONObject object = objectData.getJSONObject("data");
            scheme.setLevel(object.getInteger("level"));
            scheme.setShowLevel(object.getString("showLevel"));
            scheme.setOnlineDiscount(object.getString("onlineDiscount"));
            scheme.setReturnWater(object.getString("returnWater"));
            scheme.setDepositDiscountScheme(object.getString("depositDiscountScheme"));
        }catch (Exception e){
            ApiLogger.error(String.format("assemble member prefer scheme error. data: %s", data), e);
        }
        return scheme;
    }

    /**
     * 查询层级列表
     * @param body
     * @param caller
     * @return
     */
    public EGResp findLevelList(String body, String caller) {
        EGReq req = assembleEGReq(CmdType.CONFIG, 0x500029, body);
        return thriftFactory.call(req, caller);
    }

    /**
     * 查询层级映射列表
     * @param body
     * @param caller
     * @return
     */
    public EGResp findLevelListSimple(String body, String caller) {
        EGReq req = assembleEGReq(CmdType.CONFIG, 0x500029, body);
        return thriftFactory.call(req, caller);
    }

    /**
     * 查询会员反水方案列表
     * @param body
     * @param caller
     * @return
     */
    public EGResp findReturnWaters(String body, String caller) {
        //TODO 修改cmdType 和 cmd值
        EGReq req = assembleEGReq(CmdType.CONFIG, 0x100005, body);
        return thriftFactory.call(req, caller);
    }

    /**
     * 添加代理参数配置
     * @param body
     * @param caller
     * @return
     */
    public EGResp addAgentConfig(String body, String caller) {
        //TODO 修改cmdType 和 cmd值
        EGReq req = assembleEGReq(CmdType.CONFIG, 0x500041, body);
        return thriftFactory.call(req, caller);
    }

    /**
     * 更新代理参数配置
     * @param body
     * @param caller
     * @return
     */
    public EGResp updateAgentConfig(String body, String caller) {
        //TODO 修改cmdType 和 cmd值
        EGReq req = assembleEGReq(CmdType.CONFIG, 0x500042, body);
        return thriftFactory.call(req, caller);
    }

    /**
     * 资金回收
     * @param uid
     * @return
     */
    public boolean backMoney(long uid){
        String body = "{\"userId\":" + uid + "}";
        EGReq req = assembleEGReq(CmdType.GAME, 0x600003, body);
        try {
            EGResp call = thriftFactory.call(req, UserContants.CALLER);
            return Optional.ofNullable(call).filter(code -> code.getCode() == 0).isPresent();
        }catch (Exception e){
            ApiLogger.error(String.format("back moeny error. uid: %d", uid), e);
        }
        return false;
    }

    /**
     * 组装thrift请求对象
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
     * 注册支付账号
     *
     * @param body
     * @return
     */
    public boolean registerPaymentAcccount(String body){
        EGReq req = assembleEGReq(CmdType.SETTLE, 0x300002, body);
        try {
            EGResp call = thriftFactory.call(req, UserContants.CALLER);
            return Optional.ofNullable(call).filter(code -> call.getCode() == 0).isPresent();
        }catch (Exception e){
            ApiLogger.error(String.format("register payment account error. req: %s", JSON.toJSONString(req)), e);
        }
        return false;
    }

    /**
     * 获取业主平台的授信额度和已用额度
     * @param body
     * @param caller
     * @return
     */
    public EGResp getOwnerAccountLimit(String body,String caller){
        //TODO 修改cmdType 和 cmd值
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100005, body);
        return thriftFactory.call(req, caller);
    }

    /**
     * 设置会员层级
     *
     * @param member
     * @return
     */
    public long settingLevel(Member member){
        String body = assembleBody(member);
        EGReq req = assembleEGReq(CmdType.CONFIG, 0x500081, body);
        try {

            EGResp call = thriftFactory.call(req, UserContants.CALLER);
            if (Optional.ofNullable(call).filter(code -> call.getCode() == 0).isPresent()){
                JSONObject object = JSONObject.parseObject(call.getData());
                if (Optional.ofNullable(object).filter(level -> object.getLong("levelId") != null).isPresent()) {
                    return object.getLongValue("levelId");
                }
            }
        }catch (Exception e){
            ApiLogger.error(String.format("setting member level error. req: %s", JSON.toJSONString(req)), e);
        }
        return 0L;
    }
    
    /**
     * 获取会员详情
     *
     * @param userId
     * @return
     */
    public long getMemberLevel(Long userId){
        String body = "{\"userId\":" + userId + "}";
        EGReq req = assembleEGReq(CmdType.CONFIG, 0x500082, body);
        try {

            EGResp call = thriftFactory.call(req, UserContants.CALLER);
            if (Optional.ofNullable(call).filter(code -> call.getCode() == 0).isPresent()){
                JSONObject object = JSONObject.parseObject(call.getData());
                if (Optional.ofNullable(object).filter(level -> object.getLong("levelId") != null).isPresent()) {
                    return object.getLongValue("lelve");
                }
            }
        }catch (Exception e){
            ApiLogger.error(String.format("get member level error. req: %s", JSON.toJSONString(req)), e);
        }
        return 0L;
    }

    /**
     * 组装body
     *
     * @param member
     * @return
     */
    private String assembleBody(Member member) {
        JSONObject object = new JSONObject();
        object.put("UserId", member.getMemberId());
        object.put("UserName", member.getUsername());
        object.put("OwnerId", member.getOwnerId());
        object.put("AgentId", member.getAgentId());
        object.put("RegisterTime", member.getRegisterTime());
        return object.toJSONString();
    }

    /**
     * 更新会员层级
     *
     * @param member
     * @param level
     * @return
     */
    public boolean setMemberLevel(Member member, Long level){
        try {
            String body = assembleBody(member, level);
            EGResp call = thriftFactory.call(CmdType.CONFIG, 0x50002d, body, UserContants.CALLER);
            return Optional.ofNullable(call).filter(code -> code.getCode() == 0).isPresent();
        }catch (Exception e){
            ApiLogger.error(String.format("set member level error. member: %s, level: %d", JSON.toJSONString(member), level), e);
        }
        return false;
    }

    /**
     * 组装请求数据
     *
     * @param member
     * @param level
     * @return
     */
    private String assembleBody(Member member, Long level) {
        JSONObject body = new JSONObject();
        body.put("OwnerId", member.getOwnerId());
        body.put("UserLevelId", level);
        body.put("members", new String[]{member.getUsername()});
        return body.toJSONString();
    }

    /**
     * 组装会员反水记录列表
     * @param body
     * @param account
     * @return
     */
    public EGResp getMemberReturnWater(String body, String account) {
        EGReq req = assembleEGReq(CmdType.CONFIG, 0x500044, body);
        try {
            EGResp call = thriftFactory.call(req, UserContants.CALLER);
            return call;
        }catch (Exception e){
            ApiLogger.error(String.format("setting member return water. req: %s", JSON.toJSONString(req)), e);
        }
        return null;
    }

    public Long getOwnerLimited(Long ownerId) {
        Long result = null;
        EGReq req = assembleEGReq(CmdType.CONFIG, 0x500016, "{\"OwnerId\":"+ownerId+"}");
        try {
            EGResp call = thriftFactory.call(req, UserContants.CALLER);
            ApiLogger.info(call.getData());
            return  Long.valueOf(call.getData());
        }catch (Exception e){
            ApiLogger.error(String.format("setting member return water. req: %s", JSON.toJSONString(req)), e);
        }
        return result;
    }
}
