package com.magic.user.service;

import com.magic.user.entity.AgentConfig;

import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:24
 */
public interface AgentConfigService {

    long add(AgentConfig agentConfig);

    Map<String, Object> get(long id);

    Map<String, Object> findByAgentId(long agentId);

    int update(AgentConfig agentConfig);
}
