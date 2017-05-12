package com.magic.user.service;

import com.magic.user.entity.AgentConfig;
import com.magic.user.storage.AgentConfigDbService;
import com.magic.user.vo.AgentConfigVo;
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
    public AgentConfig get(Long id) {
        return agentConfigDbService.get(id);
    }

    @Override
    public AgentConfigVo findByAgentId(Long agentId) {
        return (AgentConfigVo) agentConfigDbService.get("findByAgentId", null, agentId);
    }

    @Override
    public int update(AgentConfig agentConfig) {
        return agentConfigDbService.update(agentConfig);
    }
}
