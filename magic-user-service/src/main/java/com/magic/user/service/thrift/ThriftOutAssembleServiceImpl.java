package com.magic.user.service.thrift;

import com.alibaba.fastjson.JSON;
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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

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
        if (!StringUtils.isEmpty(balance)){
            balance = "0";
        }
        return balance;
    }

    /**
     * 获取会员的余额列表
     * @param body
     * @param caller
     * @return
     */
    public EGResp getMemberBalances(String body, String caller) {
        //TODO 修改cmdType 和 cmd值
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100005, body);
        return thriftFactory.call(req, caller);
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
     * @param body
     * @param caller
     * @return
     */
    public EGResp getAgentConfig(String body, String caller) {
        //TODO 修改cmdType 和 cmd值
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100005, body);
        return thriftFactory.call(req, caller);
    }

    /**
     * 查询某个会员的优惠方案
     * @param body
     * @param caller
     * @return
     */
    public EGResp getMemberPrivilege(String body, String caller) {
        //TODO 修改cmdType 和 cmd值
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100005, body);
        return thriftFactory.call(req, caller);
    }

    /**
     * 查询层级列表
     * @param body
     * @param caller
     * @return
     */
    public EGResp findLevelList(String body, String caller) {
        //TODO 修改cmdType 和 cmd值
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100005, body);
        return thriftFactory.call(req, caller);
    }

    /**
     * 查询层级映射列表
     * @param body
     * @param caller
     * @return
     */
    public EGResp findLevelListSimple(String body, String caller) {
        //TODO 修改cmdType 和 cmd值
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100005, body);
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
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100005, body);
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
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100005, body);
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
        EGReq req = assembleEGReq(CmdType.PASSPORT, 0x100005, body);
        return thriftFactory.call(req, caller);
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

}
