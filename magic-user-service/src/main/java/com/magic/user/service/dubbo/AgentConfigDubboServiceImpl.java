package com.magic.user.service.dubbo;

import com.magic.api.commons.ApiLogger;
import com.magic.user.dao.AgentConfigDao;
import com.magic.user.service.AgentConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AgentConfigDubboService
 *
 * @author aaron
 * @date 2017/7/31
 */
@Service("agentConfigDubboService")
public class AgentConfigDubboServiceImpl implements AgentConfigDubboService {
    @Resource
    private AgentConfigService agentConfigService;

    @Override
    public Map<String, Integer> getProxysByAgentConfig(List<String> domains) {
        if (domains == null || domains.size() <= 0) {
            return new HashMap<String, Integer>();
        }
        ApiLogger.info("#####getProxysByAgentConfig method params is " + domains + "#####");
        try {
            Map result = new HashMap();
            List<Map<String, Object>> list = (List<Map<String, Object>>) agentConfigService.getAgentByDomain(domains);
            if (list != null && list.size() > 0) {
                for (Map<String, Object> map : list) {
                    String domain = null;
                    Integer proxyNum = null;
                    for (String key : map.keySet()) {
                        if ("domain".equals(key)) {
                            domain = map.get(key) == null ? null : map.get(key).toString();
                        } else {
                            proxyNum = Integer.valueOf(map.get(key) == null ? "1" : map.get(key).toString());
                        }
                    }
                    result.put(domain, proxyNum);
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<String, Integer>();
    }
}
