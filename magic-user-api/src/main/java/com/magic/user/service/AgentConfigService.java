package com.magic.user.service;

import com.magic.user.entity.AgentConfig;
import com.magic.user.vo.AgentConfigVo;

import java.util.List;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:24
 */
public interface AgentConfigService {

    /**
     * @Doc 添加代理配置
     * @param agentConfig
     * @return
     */
    boolean add(AgentConfig agentConfig);

    /**
     * @Doc 获取代理配置
     * @param id
     * @return
     */
    AgentConfig get(Long id);

    /**
     * @Doc 根据代理id获取代理配置
     * @param agentId
     * @return
     */
    AgentConfigVo findByAgentId(Long agentId);

    /**
     * @Doc 修改代理配置
     * @param agentConfig
     * @return
     */
    boolean update(AgentConfig agentConfig);

    /**
     * 获取代理下的域名
     * @param agentId
     * @return
     */
    List<String> getAgentDomain(Long agentId);

    /**
     * 获取域名下的代理数量
     * @param domain
     * @return List<Map>
     */
    List<Map<String,Integer>> getAgentByDomain(List<String> domain);
    /**
     * 获取域名下的代理信息
     * @param domain
     * @return List<Map>
     */
    List<AgentConfig> getAgentConfigByDomain(String domain);
}
