package com.magic.user.service.dubbo;

import com.magic.user.dao.AgentConfigDao;
import com.magic.user.service.AgentConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
/**
 * AgentConfigDubboService
 *
 * @author aaron
 * @date 2017/7/31
 */
@Service("agentConfigDubboService")
public class AgentConfigDubboServiceImpl implements AgentConfigDubboService{
    @Resource
    private AgentConfigService agentConfigService;
    @Override
    public List<Map<String, Integer>> getProxysByAgentConfig(List<String> domains) {
        return agentConfigService.getAgentByDomain(domains);
    }
}
