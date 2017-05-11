package com.magic.user.agent.resource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.model.Page;
import com.magic.api.commons.tools.IPUtil;
import com.magic.api.commons.tools.UUIDUtil;
import com.magic.service.java.UuidService;
import com.magic.user.agent.resource.service.AgentResourceService;
import com.magic.user.constants.UserContants;
import com.magic.user.entity.*;
import com.magic.user.enums.AccountStatus;
import com.magic.user.enums.AccountType;
import com.magic.user.enums.ReviewStatus;
import com.magic.user.service.*;
import com.magic.user.vo.UserCondition;
import com.sun.javafx.binding.StringConstant;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.swing.text.StringContent;
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
    @Resource
    private AccountIdMappingService accountIdMappingService;


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
        return UserContants.EMPTY_STRING;
    }

    @Override
    public String add(RequestContext rc, Long holder, String account, String password, String realname, String telephone, String bankCardNo, String email, Integer returnScheme,
                      Integer adminCost, Integer feeScheme, String[] domain, Integer discount, Integer cost) {

        String generalizeCode = UUIDUtil.getCode();
        long userId = uuidService.assignUid();
        User user = new User(userId, realname, account, telephone, email, AccountType.agent, System.currentTimeMillis(), IPUtil.ipToInt(rc.getIp()), generalizeCode, AccountStatus.enable, bankCardNo);
        //1、添加代理基础信息
        long count = userService.addAgent(user);
        if (count > 0) {
            Login login = new Login(userId, account, password);
            //TODO 此处插入失败如何处理
            //2、天机代理登录信息
            if (loginService.add(login) <= 0) {
                ApiLogger.error("add user login failed,userId:" + userId);
            }
            String domainSpit = com.magic.user.utils.StringUtils.arrayToStrSplit(domain);
            AgentConfig agentConfig = assembleAgentConfig(userId, returnScheme, adminCost, feeScheme, domainSpit, discount, cost);
            //3、添加代理配置
            if (agentConfigService.add(agentConfig) <= 0) {
                ApiLogger.error("add agentConfig failed,agentId:" + userId);
            }
            User holderUser = userService.get(holder);
            OwnerStockAgentMember ownerStockAgentMember = assembleOwnerStockAgent(holderUser.getOwnerId(), holder, userId);
            //4、添加业主映射信息
            if (ownerStockAgentService.add(ownerStockAgentMember) <= 0)
                ApiLogger.error(String.format("add agentConfig failed,ownerId:%d,stockId:%d,agentId:%d", holderUser.getOwnerId(), holder, userId));
            OwnerAccountUser ownerAccountUser = new OwnerAccountUser(holderUser.getOwnerId() + UserContants.SPLIT_LINE + account, userId);
            if (accountIdMappingService.add(ownerAccountUser) <= 0) {
                //todo
            }
            JSONObject result = new JSONObject();
            result.put("id", userId);
            return result.toJSONString();
        }
        return UserContants.EMPTY_STRING;
    }

    private OwnerStockAgentMember assembleOwnerStockAgent(Long ownerId, Long stockId, Long agentId) {
        OwnerStockAgentMember ownerStockAgentMember = new OwnerStockAgentMember();
        ownerStockAgentMember.setOwnerId(ownerId);
        ownerStockAgentMember.setStockId(stockId);
        ownerStockAgentMember.setAgentId(agentId);
        ownerStockAgentMember.setMemNumber(0);
        return ownerStockAgentMember;
    }

    private AgentConfig assembleAgentConfig(Long agentId, Integer returnSchemeId, Integer adminCostId, Integer feeId, String domain, Integer discount, Integer cost) {
        AgentConfig agentConfig = new AgentConfig();
        agentConfig.setAgentId(agentId);
        agentConfig.setReturnSchemeId(returnSchemeId);
        agentConfig.setAdminCostId(adminCostId);
        agentConfig.setFeeId(feeId);
        agentConfig.setDomain(domain);
        agentConfig.setDiscount(discount);
        agentConfig.setCost(cost);
        return agentConfig;
    }

    @Override
    public String getDetail(RequestContext rc, Long id) {
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
    public String resetPwd(RequestContext rc, Long id, String password) {
        boolean flag = loginService.resetPassword(id, password);
        if (!flag)
            ApiLogger.error("update agent password failed,userId:" + id);
        return UserContants.EMPTY_STRING;
    }

    @Override
    public String update(RequestContext rc, Long id, String realname, String telephone, String email, String bankCardNo, String bank) {
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
        return UserContants.EMPTY_STRING;
    }

    @Override
    public String updateAgentConfig(RequestContext rc, Long agentId, Integer returnScheme, Integer adminCost, Integer feeScheme) {
        AgentConfig agentConfig = new AgentConfig(agentId, returnScheme, adminCost, feeScheme);
        int count = agentConfigService.update(agentConfig);
        if (count <= 0) {
            //TODO throw
        }
        return UserContants.EMPTY_STRING;
    }

    @Override
    public String agentApply(RequestContext rc, String account, String password, String realname, String telephone, String email, String bankCardNo) {
        //TODO 获取股东id
        long stockId = 0;
        //TODO 获取加入来源
        String resourceUrl = "";
        int ip = IPUtil.ipToInt(rc.getIp());
        AgentApply agentApply = new AgentApply(account, realname, password, stockId, telephone, email, ReviewStatus.noReview, resourceUrl, ip, System.currentTimeMillis());
        long count = agentApplyService.add(agentApply);
        if (count <= 0) {
            //Todo throw
        }
        return UserContants.EMPTY_STRING;
    }

    @Override
    public String agentApplyList(RequestContext rc, String account, Integer status, Integer page, Integer count) {
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
    public String agentApplyInfo(RequestContext rc, Long applyId) {
        Map<String, Object> baseInfo = agentApplyService.agentReviewInfo(applyId);
        JSONObject result = new JSONObject();
        result.put("baseInfo", baseInfo);
        return result.toJSONString();
    }

    @Override
    public String agentReview(RequestContext rc, Long id, Integer reviewStatus, Long holder, String realname, String telephone, String bankCardNo, String email, Integer returnScheme, Integer adminCost, Integer feeScheme, String[] domain, Integer discount, Integer cost) {
        //1、如果拒绝，修改申请状态，增加审核信息
        if (reviewStatus == ReviewStatus.noPass.value()) {
            int count = agentApplyService.updateStatus(id, reviewStatus);
            if (count > 0) {
                User user = userService.get(rc.getUid());
                AgentReview agentReview = new AgentReview(id, realname, rc.getUid(), user.getUsername(), ReviewStatus.parse(reviewStatus), System.currentTimeMillis());
                if (agentReviewService.add(agentReview) <= 0) {
                    //TOdo
                }
            }
        } else if (reviewStatus == ReviewStatus.pass.value()) {//2、通过，修改申请状态，添加历史记录，添加代理信息
            int count = agentApplyService.updateStatus(id, reviewStatus);
            User procUser = userService.get(rc.getUid());
            //todo
            String procUserName = "aaa";
            AgentReview agentReview = new AgentReview(id, realname, rc.getUid(), procUserName, ReviewStatus.parse(reviewStatus), System.currentTimeMillis());
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
        return UserContants.EMPTY_STRING;
    }

    @Override
    public String disable(RequestContext rc, Long agentId, Integer status) {
        int count = userService.disable(agentId, status);
        if (count <= 0) {
            //todo
        }
        return UserContants.EMPTY_STRING;
    }


}
