package com.magic.user.dao;

import com.magic.user.bean.AgentCondition;
import com.magic.user.vo.AgentConditionVo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: joey
 * Date: 2017/5/17
 * Time: 16:18
 */
@Component
public class AgentMongoDaoImpl extends BaseMongoDAOImpl<AgentConditionVo> {

    /**
     * @param agentId
     * @param status
     * @return
     * @Doc 修改代理状态
     */
    public boolean updateStatus(Long agentId, Integer status) {
        Query query = new Query(new Criteria("agentId").is(agentId));
        Update update = new Update().set("status", status);
        return super.update(query, update) != null;
    }


    /**
     * @param agentId
     * @return
     * @Doc 根据代理id获取代理信息
     */
    public AgentConditionVo get(Long agentId) {
        Query query = new Query().addCriteria(new Criteria("agentId").is(agentId));
        return super.findOne(query);
    }

    /**
     * @param userCondition
     * @param page
     * @param count
     * @return
     * @Doc 分页查询代理信息
     */
    public List<AgentConditionVo> queryByPage(AgentCondition userCondition, Integer page, Integer count) {
        if (userCondition != null) {
            Query query = assembleQuery(userCondition);
            if (page != null && count != null) {
                query.skip((page - 1) * count );
                query.limit(count);
                query.with(new Sort(Sort.Direction.DESC, "registerTime"));
            }
            List<AgentConditionVo> result = super.find(query);
            return result;
        }
        return null;
    }

    /**
     * @param userCondition
     * @return
     * @Doc 查询代理总数量
     */
    public long getCount(AgentCondition userCondition) {
        Query query = assembleQuery(userCondition);
        long count = super.count(query);
        return count;
    }

    /**
     * @param userCondition
     * @return
     * @Doc 组装代理查询条件
     */
    private Query assembleQuery(AgentCondition userCondition) {
        Query query = new Query();
        if (userCondition != null) {
            query.addCriteria(new Criteria("ownerId").is(userCondition.getOwnerId()));
            if (userCondition.getRegister() != null) {
                Long start = userCondition.getRegister().getStart();
                Long end = userCondition.getRegister().getEnd();
                if (start != null ) {
                    query.addCriteria(new Criteria("registerTime").gte(start));
                }
                if (end != null ) {
                    query.addCriteria(new Criteria("registerTime").lte(end));
                }
            }
            if (userCondition.getMembers() != null) {
                Long min = userCondition.getMembers().getMin();
                Long max = userCondition.getMembers().getMax();
                if (min != null ) {
                    query.addCriteria(new Criteria("members").gte(min));
                }
                if (max != null) {
                    query.addCriteria(new Criteria("members").lte(max));
                }
            }
            if (userCondition.getDepositMoney() != null) {
                Long min = userCondition.getDepositMoney().getMin();
                Long max = userCondition.getDepositMoney().getMax();
                if (min != null ) {
                    query.addCriteria(new Criteria("depositMoney").gte(min));
                }
                if (max != null ) {
                    query.addCriteria(new Criteria("depositMoney").lte(max));
                }
            }
            if (userCondition.getWithdrawMoney() != null) {
                Long min = userCondition.getWithdrawMoney().getMin();
                Long max = userCondition.getWithdrawMoney().getMax();
                if (min != null ) {
                    query.addCriteria(new Criteria("withdrawMoney").gte(min));
                }
                if (max != null) {
                    query.addCriteria(new Criteria("withdrawMoney").lte(max));
                }
            }
            if (userCondition.getStatus() != null && userCondition.getStatus() > 0) {
                query.addCriteria(new Criteria("status").lte(userCondition.getStatus()));
            }
        }
        return query;
    }


}
