package com.magic.user.service;

import com.magic.user.bean.AgentCondition;
import com.magic.user.vo.AgentConditionVo;

import java.util.List;

/**
 * User: joey
 * Date: 2017/5/17
 * Time: 16:25
 */
public interface AgentMongoService {

    /**
     * @Doc 修改代理状态
     * @param agentId
     * @param status
     * @return
     */
    boolean updateStatus(Long agentId, Integer status);

    /**
     * 添加代理信息
     * @param agentConditionVo
     * @return
     */
    boolean saveAgent(AgentConditionVo agentConditionVo);

    /**
     * @Doc 获取代理信息
     * @param agentId
     * @return
     */
    AgentConditionVo get(Long agentId);

    /**
     * @Doc 分页查询代理信息
     * @param userCondition
     * @param page
     * @param count
     * @return
     */
    List<AgentConditionVo> queryByPage(AgentCondition userCondition, Integer page, Integer count);

    /**
     * @Doc 获取代理总数
     * @param userCondition
     * @return
     */
    long getCount(AgentCondition userCondition);
}
