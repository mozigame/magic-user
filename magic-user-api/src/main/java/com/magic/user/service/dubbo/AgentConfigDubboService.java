package com.magic.user.service.dubbo;

import java.util.List;
import java.util.Map;
/**
 * AgentConfigDubboService
 *
 * @author aaron
 * @date 2017/7/31
 */
public interface AgentConfigDubboService {
    List<Map<String,Integer>> getProxysByAgentConfig(List<String> domains);
}
