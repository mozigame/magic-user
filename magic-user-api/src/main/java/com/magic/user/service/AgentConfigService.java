package com.magic.user.service;

import com.magic.user.entity.AgentConfig;
import com.magic.user.vo.AgentConfigVo;

import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:24
 */
public interface AgentConfigService {

    long add(AgentConfig agentConfig);

    AgentConfig get(Long id);

    AgentConfigVo findByAgentId(Long agentId);

    int update(AgentConfig agentConfig);
}
