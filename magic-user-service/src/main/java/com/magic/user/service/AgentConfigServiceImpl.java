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
    public boolean add(AgentConfig agentConfig) {
        long result = agentConfigDbService.insert(agentConfig);
        return result > 0 ;
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
    public boolean update(AgentConfig agentConfig) {
        int result = agentConfigDbService.update(agentConfig);
        return result > 0;
    }
}
