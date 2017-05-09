package com.magic.user.agent.resource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.magic.user.agent.resource.service.AgentConfigResourceService;
import com.magic.user.entity.AgentConfig;
import com.magic.user.service.AgentConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 21:53
 */
@Service("agentConfigResourceService")
public class AgentConfigResourceServiceImpl implements AgentConfigResourceService {


    @Resource(name = "agentConfigService")
    private AgentConfigService agentConfigService;

    @Override
    public String add(AgentConfig agentConfig) {
        long id = agentConfigService.add(agentConfig);
        if (id > 0) {
//            JSONObject
        }
        return null;
    }
}
