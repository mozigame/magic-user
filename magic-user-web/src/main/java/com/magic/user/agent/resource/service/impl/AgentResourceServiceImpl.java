package com.magic.user.agent.resource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.model.Page;
import com.magic.api.commons.tools.IPUtil;
import com.magic.api.commons.tools.UUIDUtil;
import com.magic.service.java.UuidService;
import com.magic.user.agent.resource.service.AgentResourceService;
import com.magic.user.entity.*;
import com.magic.user.enums.AccountStatus;
import com.magic.user.enums.AccountType;
import com.magic.user.enums.ReviewStatus;
import com.magic.user.service.*;
import com.magic.user.vo.UserCondition;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 20:10
 */
@Service("agentResourceService")
public class AgentResourceServiceImpl implements AgentResourceService {

    @Resource(name = "agentConfigService")
    private AgentConfigService agentConfigService;
    @Resource(name = "ownerStockAgentService")
    private OwnerStockAgentService ownerStockAgentService;
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "loginService")
    private LoginService loginService;
    @Resource(name = "agentApplyService")
    private AgentApplyService agentApplyService;
    @Resource(name = "agentReviewService")
    private AgentReviewService agentReviewService;
    @Resource
    private UuidService uuidService;


    @Override
    public String findByPage(UserCondition userCondition) {
        List<Map<String, Object>> list = userService.findAgentByPage(userCondition);
        if (list != null && list.size() > 0) {
            for (Map<String, Object> map : list) {
                map.put("showStatus", AccountStatus.parse((Integer) map.get("status")).desc());
            }
            long count = userService.getAgentCount(userCondition);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("page", userCondition.getPageNo());
            jsonObject.put("count", userCondition.getPageSize());
            jsonObject.put("total", count);
            jsonObject.put("list", list);
            return jsonObject.toJSONString();
        }
        return null;
    }

    @Override
    public String add(RequestContext rc, long holder, String account, String password, String realname, String telephone, String bankCardNo, String email, int returnScheme,
                      int adminCost, int feeScheme, String[] domain, int discount, int cost) {

        String generalizeCode = UUIDUtil.getCode();
        long userId = uuidService.assignUid();
        User user = new User(userId, realname, account, telephone, email, AccountType.agent, new Date(), IPUtil.ipToInt(rc.getIp()), generalizeCode, AccountStatus.enable, bankCardNo);
        long count = userService.addAgent(user);
        if (count > 0) {
            Login login = new Login(userId, account, password);
            //TODO 此处插入失败如何处理
            if (loginService.add(login) <= 0) {
                ApiLogger.error("add user login failed,userId:" + userId);
            }
            String domainSpit = com.magic.user.utils.StringUtils.arrayToStrSplit(domain);
            AgentConfig agentConfig = new AgentConfig(userId, returnScheme, adminCost, feeScheme, domainSpit);
            if (agentConfigService.add(agentConfig) <= 0) {
                ApiLogger.error("add agentConfig failed,agentId:" + userId);
            }
            long ownerId = userService.getOwnerIdByStock(holder);
            OwnerStockAgentMember ownerStockAgentMember = new OwnerStockAgentMember(ownerId, holder, userId);
            if (ownerStockAgentService.add(ownerStockAgentMember) <= 0)
                ApiLogger.error(String.format("add agentConfig failed,ownerId:%d,stockId:%d,agentId:%d", ownerId, holder, userId));
            JSONObject result = new JSONObject();
            result.put("id", userId);
            return result.toJSONString();
        }
        return null;
    }

    @Override
    public String getDetail(RequestContext rc, long id) {
        JSONObject result = new JSONObject();
        Map<String, Object> agentInfo = userService.getAgentDetail(id);
        if (agentInfo != null) {
            agentInfo.put("showStatus", AccountStatus.parse((Integer) agentInfo.get("showStatus")).desc());
        }
        //todo 代理参数配置获取中文通过调用接口
        Map<String, Object> agentConfig = agentConfigService.findByAgentId(id);
        result.put("baseInfo", agentInfo);
        result.put("settings", agentConfig);
        //TODO 本期资金状况
        String fundProfile = "{\n" +
                "    \"syncTime\": \"2017-04-18 09:29:33\",\n" +
                "    \"info\": {\n" +
                "        \"members\": 490,\n" +
                "        \"depositMembers\": 410,\n" +
                "        \"depositTotalMoney\": \"29006590\",\n" +
                "        \"withdrawTotalMoney\": \"24500120\",\n" +
                "        \"betTotalMoney\": \"20900067\",\n" +
                "        \"betEffMoney\": \"19007689\",\n" +
                "        \"gains\": \"4908763\"\n" +
                "    }\n" +
                "}";
        result.put("fundProfile", JSONObject.parseObject(fundProfile));
        return result.toJSONString();
    }

    @Override
    public String resetPwd(RequestContext rc, long id, String password) {
        boolean flag = loginService.resetPassword(id, password);
        if (!flag)
            ApiLogger.error("update agent password failed,userId:" + id);
        return "";
    }

    @Override
    public String update(RequestContext rc, long id, String realname, String telephone, String email, String bankCardNo, int status) {

        return null;
    }

    @Override
    public String update(RequestContext rc, long id, String realname, String telephone, String email, String bankCardNo, String bank) {
        User user = new User();
        user.setUserId(id);
        user.setRealname(realname);
        user.setTelephone(telephone);
        user.setEmail(email);
        user.setBankCardNo(bankCardNo);
        user.setBank(bank);
        int count = userService.update(user);
        if (count <= 0) {
            //TODO throw
            ApiLogger.error("update agent info error,userId:" + id);
        }
        return "";
    }

    @Override
    public String updateAgentConfig(RequestContext rc, long agentId, int returnScheme, int adminCost, int feeScheme) {
        AgentConfig agentConfig = new AgentConfig(agentId, returnScheme, adminCost, feeScheme);
        int count = agentConfigService.update(agentConfig);
        if (count <= 0) {
            //TODO throw
        }
        return "";
    }

    @Override
    public String agentApply(RequestContext rc, String account, String password, String realname, String telephone, String email, String bankCardNo) {
        //TODO 获取股东id
        long stockId = 0;
        //TODO 获取加入来源
        String resourceUrl = "";
        int ip = IPUtil.ipToInt(rc.getIp());
        AgentApply agentApply = new AgentApply(account, realname, password, stockId, telephone, email, ReviewStatus.noReview, resourceUrl, ip, new Date());
        long count = agentApplyService.add(agentApply);
        if (count <= 0) {
            //Todo throw
        }
        return "";
    }

    @Override
    public String agentApplyList(RequestContext rc, String account, int status, int page, int count) {
        Page pageRes = new Page();
        pageRes.setPageNo(page);
        pageRes.setPageSize(count);
        AgentApply agentApply = new AgentApply();
        agentApply.setUsername(account);
        agentApply.setStatus(ReviewStatus.parse(status));
        pageRes = agentApplyService.findByPage(agentApply, pageRes);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("page", pageRes.getPageNo());
        jsonObject.put("count", pageRes.getPageSize());
        jsonObject.put("total", pageRes.getTotalCount());
        jsonObject.put("list", pageRes.getResult());
        return jsonObject.toJSONString();
    }

    @Override
    public String agentApplyInfo(RequestContext rc, long applyId) {
        Map<String, Object> baseInfo = agentApplyService.agentReviewInfo(applyId);
        JSONObject result = new JSONObject();
        result.put("baseInfo", baseInfo);
        return result.toJSONString();
    }

    @Override
    public String agentReview(RequestContext rc, long id, int reviewStatus, long holder, String realname, String telephone, String bankCardNo, String email, int returnScheme, int adminCost, int feeScheme, String[] domain, int discount, int cost) {
        //1、如果拒绝，修改申请状态，增加审核信息
        if (reviewStatus == ReviewStatus.noPass.value()) {
            int count = agentApplyService.updateStatus(id, reviewStatus);
            if (count > 0) {
                User user = userService.get(rc.getUid());
                AgentReview agentReview = new AgentReview(id, realname, rc.getUid(), user.getUsername(), ReviewStatus.parse(reviewStatus), new Date());
                if (agentReviewService.add(agentReview) <= 0) {
                    //TOdo
                }
            }
        } else if (reviewStatus == ReviewStatus.pass.value()) {//2、通过，修改申请状态，添加历史记录，添加代理信息
            int count = agentApplyService.updateStatus(id, reviewStatus);
            User procUser = userService.get(rc.getUid());
            //todo
            String procUserName="aaa";
            AgentReview agentReview = new AgentReview(id, realname, rc.getUid(), procUserName, ReviewStatus.parse(reviewStatus), new Date());
            //添加审核历史记录
            if (agentReviewService.add(agentReview) <= 0) {
                //TOdo
            }
            //添加用户
            if (count > 0) {
                AgentApply agentApply = agentApplyService.get(id);
                String generalizeCode = UUIDUtil.getCode();
                long userId = uuidService.assignUid();
                User userAgent = new User(userId, realname, agentApply.getUsername(), agentApply.getTelephone(), agentApply.getEmail(), AccountType.agent, agentApply.getCreateTime(), IPUtil.ipToInt(rc.getIp()), generalizeCode, AccountStatus.enable, agentApply.getBankCardNo());
                long addAgentCount = userService.addAgent(userAgent);
                if (addAgentCount <= 0) {
                    //todo
                }
            }
        }
        return "";
    }

    @Override
    public String disable(RequestContext rc, long agentId, int status) {
        int count = userService.disable(agentId, status);
        if (count <= 0) {
            //todo
        }
        return "";
    }


}
