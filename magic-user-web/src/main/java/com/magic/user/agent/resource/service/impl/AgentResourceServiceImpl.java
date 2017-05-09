package com.magic.user.agent.resource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.tools.IPUtil;
import com.magic.api.commons.utils.StringUtils;
import com.magic.user.agent.resource.AgentResource;
import com.magic.user.agent.resource.service.AgentResourceService;
import com.magic.user.entity.AgentConfig;
import com.magic.user.entity.User;
import com.magic.user.enums.AccountStatus;
import com.magic.user.service.AgentService;
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

    @Resource(name = "agentService")
    private AgentService agentService;

    @Override
    public String findByPage(UserCondition userCondition) {
        List<Map<String, Object>> list = agentService.findByPage(userCondition);
        if (list != null && list.size() > 0) {
            long count = agentService.getCount(userCondition);
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

        //TODO
        String generalizeCode = "";
        User user = new User(realname, account, telephone, email, new Date(), IPUtil.ipToInt(rc.getIp()), generalizeCode, AccountStatus.enable, bankCardNo);
        long userId = agentService.add(user);
        if (userId > 0) {
            AgentConfig agentConfig = new AgentConfig(userId, returnScheme, adminCost, feeScheme, com.magic.user.utils.StringUtils.arrayToStrSplit(domain));
            JSONObject result = new JSONObject();
            result.put("id", userId);
            return result.toJSONString();
        }
        return null;
    }
}
