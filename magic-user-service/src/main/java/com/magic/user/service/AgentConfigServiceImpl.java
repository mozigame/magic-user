package com.magic.user.service;

import com.magic.user.entity.AgentConfig;
import com.magic.user.storage.AgentConfigDbService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 21:54
 */
@Service("agentConfigService")
public class AgentConfigServiceImpl implements AgentConfigService {

    @Resource(name = "agentConfigDbService")
    private AgentConfigDbService agentConfigDbService;

    @Override
    public long add(AgentConfig agentConfig) {
        return agentConfigDbService.insert(agentConfig);
    }

    @Override
    public Map<String, Object> get(long id) {
        return (Map<String, Object>) agentConfigDbService.get(id);
    }

    @Override
    public Map<String, Object> findByAgentId(long agentId) {
        return (Map<String, Object>) agentConfigDbService.get("findByAgentId", null, agentId);
    }

    @Override
    public int update(AgentConfig agentConfig) {
        return agentConfigDbService.update(agentConfig);
    }
}
