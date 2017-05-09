package com.magic.user.service;

import com.magic.user.entity.AgentConfig;
import com.magic.user.storage.AgentDbService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 21:54
 */
@Service("agentConfigService")
public class AgentConfigServiceImpl implements AgentConfigService {

    @Resource(name = "agentDbService")
    private AgentDbService agentDbService;

    @Override
    public long add(AgentConfig agentConfig) {
        return agentDbService.insert(agentConfig);
    }
}
