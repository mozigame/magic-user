package com.magic.user.service;

import com.magic.api.commons.ApiLogger;
import com.magic.user.bean.AgentCondition;
import com.magic.user.dao.AgentMongoDaoImpl;
import com.magic.user.vo.AgentConditionVo;
import com.magic.user.vo.AgentInfoVo;
import com.magic.user.vo.MemberConditionVo;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.management.Query;
import java.util.List;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/17
 * Time: 16:24
 */
@Service("agentMongoService")
public class AgentMongoServiceImpl implements AgentMongoService {

    @Resource
    private AgentMongoDaoImpl agentMongoDao;

    /**
     * {@inheritDoc}
     *
     * @param agentId
     * @param status
     * @return
     */
    @Override
    public boolean updateStatus(Long agentId, Integer status) {
        try {
            return agentMongoDao.updateStatus(agentId, status);
        } catch (Exception e) {
            ApiLogger.error(String.format("update agent status error.agentId:%d, status:%d", agentId, status), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @param agentConditionVo
     * @return
     */
    @Override
    public boolean saveAgent(AgentConditionVo agentConditionVo) {
        try {
            return agentMongoDao.save(agentConditionVo) != null;
        } catch (Exception e) {
            ApiLogger.error(String.format("save agent error.agentId:%d", agentConditionVo.getAgentId()), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @param agentId
     * @return
     */
    @Override
    public AgentConditionVo get(Long agentId) {
        try {
            return agentMongoDao.get(agentId);
        } catch (Exception e) {
            ApiLogger.error(String.format("get agent error.agentId:%d", agentId), e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @param userCondition
     * @param page
     * @param count
     * @return
     */
    @Override
    public List<AgentConditionVo> queryByPage(AgentCondition userCondition, Integer page, Integer count) {
        try {
            return agentMongoDao.queryByPage(userCondition, page, count);
        } catch (Exception e) {
            ApiLogger.error(String.format("query agent error.userCondition:%d", userCondition), e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @param userCondition
     * @return
     */
    @Override
    public long getCount(AgentCondition userCondition ) {
        try {
            return agentMongoDao.getCount(userCondition );
        } catch (Exception e) {
            ApiLogger.error(String.format("get agent count error.userCondition:%d", userCondition), e);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     * @param agentConditionVo
     * @return
     */
    @Override
    public boolean updateAgent(AgentConditionVo agentConditionVo) {
        try {
            return agentMongoDao.updateAgent(agentConditionVo);
        }catch (Exception e){
            ApiLogger.error(String.format("update agent  error.agentConditionVo:%d", agentConditionVo), e);
        }
        return false;
    }
}
