package com.magic.user.agent.resource.service;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.user.entity.User;
import com.magic.user.vo.UserCondition;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 20:10
 */
public interface AgentResourceService {

    String findByPage(UserCondition userCondition);

    String add(RequestContext rc, long holder, String account, String password, String realname, String telephone, String bankCardNo, String email, int returnScheme,
               int adminCost, int feeScheme, String[] domain, int discount, int cost);
}
