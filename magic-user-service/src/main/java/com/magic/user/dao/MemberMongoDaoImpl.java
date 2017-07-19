package com.magic.user.dao;

import com.alibaba.fastjson.JSON;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.utils.StringUtils;
import com.magic.user.bean.Account;
import com.magic.user.bean.MemberCondition;
import com.magic.user.enums.AccountType;
import com.magic.user.vo.AgentDepositMember;
import com.magic.user.vo.MemberConditionVo;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * User: joey
 * Date: 2017/5/17
 * Time: 15:09
 */
@Component
public class MemberMongoDaoImpl extends BaseMongoDAOImpl<MemberConditionVo> {


    /**
     * @param memberId
     * @param status
     * @return
     * @Doc 修改会员可用状态
     */
    public boolean updateMemberStatus(Long memberId, Integer status) {
        Query query = new Query(new Criteria("memberId").is(memberId));
        Update update = new Update().set("status", status);
        return super.update(query, update) != null;
    }

    /**
     * @param memberId
     * @return
     * @Doc 获取会员详情
     */
    public MemberConditionVo get(Long memberId) {
        Query query = new Query(new Criteria("memberId").is(memberId));
        return super.findOne(query);
    }

    /**
     * @param memberCondition
     * @param page
     * @param count
     * @return
     * @Doc 分页查询代理信息
     */
    public List<MemberConditionVo> queryByPage(MemberCondition memberCondition, Integer page, Integer count) {
        if (memberCondition != null) {
            Query query = assembleQuery(memberCondition);
            if (page != null && count != null) {
                query.skip((page - 1) * count);
                query.limit(count);
                query.with(new Sort(Sort.Direction.DESC, "registerTime"));
            }
            List<MemberConditionVo> result = super.find(query);
            ApiLogger.info(String.format("get data from mongo. query: %s, result: %s", JSON.toJSONString(query), JSON.toJSONString(result)));
            return result;
        }
        return null;
    }

    /**
     * @param memberCondition
     * @return
     * @Doc 获取会员总数
     */
    public long getCount(MemberCondition memberCondition) {
        if (memberCondition != null) {
            Query query = assembleQuery(memberCondition);
            return count(query);
        }
        return 0;
    }

    /**
     * @param memberCondition
     * @return
     * @Doc 组装查询会员数的条件
     */
    public Query assembleQuery(MemberCondition memberCondition) {
        Query query = new Query();
        if (memberCondition != null) {
            query.addCriteria(new Criteria("ownerId").is(memberCondition.getOwnerId()));
            if(memberCondition.getAgentId() != null){
                query.addCriteria(new Criteria("agentId").is(memberCondition.getAgentId()));
            }
            if (memberCondition.getRegister() != null) {
                Long start = memberCondition.getRegister().getStart();
                Long end = memberCondition.getRegister().getEnd();
                if (start !=null && end != null) {
                    query.addCriteria(new Criteria("registerTime").gte(start).lte(end));
                } else if (start != null) {
                    query.addCriteria(new Criteria("registerTime").gte(start));
                } else if (end != null) {
                    query.addCriteria(new Criteria("registerTime").lte(end));
                }
            }
            if (memberCondition.getDepositMoney() != null) {
                Long min = memberCondition.getDepositMoney().getMin();
                Long max = memberCondition.getDepositMoney().getMax();
                if (min != null && max != null) {
                    query.addCriteria(new Criteria("depositMoney").gte(min).lte(max));
                } else if (min != null) {
                    query.addCriteria(new Criteria("depositMoney").gte(min));
                } else if (max != null) {
                    query.addCriteria(new Criteria("depositMoney").lte(max));
                }
            }
            if (memberCondition.getWithdrawMoney() != null) {
                Long min = memberCondition.getWithdrawMoney().getMin();
                Long max = memberCondition.getWithdrawMoney().getMax();
                if (min != null && max != null) {
                    query.addCriteria(new Criteria("withdrawMoney").gte(min).lte(max));
                }else if (min !=null) {
                    query.addCriteria(new Criteria("withdrawMoney").gte(min));
                } else if (max != null) {
                    query.addCriteria(new Criteria("withdrawMoney").lte(max));
                }
            }
            if (memberCondition.getStatus() != null ) {
                query.addCriteria(new Criteria("status").is(memberCondition.getStatus()));
            }
            if (memberCondition.getLevel() != null) {
                query.addCriteria(new Criteria("level").is(memberCondition.getLevel()));
            }

            if (memberCondition.getDepositNumber() != null) {
                Long min = memberCondition.getDepositNumber().getMin();
                Long max = memberCondition.getDepositNumber().getMax();
                if (min != null && max != null) {
                    query.addCriteria(new Criteria("depositCount").gte(min).lte(max));
                } else if (min != null) {
                    query.addCriteria(new Criteria("depositCount").gte(min));
                } else if (max != null) {
                    query.addCriteria(new Criteria("depositCount").lte(max));
                }
            }
            if (memberCondition.getWithdrawNumber() != null) {
                Long min = memberCondition.getWithdrawNumber().getMin();
                Long max = memberCondition.getWithdrawNumber().getMax();
                if (min != null && max != null) {
                    query.addCriteria(new Criteria("withdrawCount").gte(min).lte(max));
                } else if (min != null) {
                    query.addCriteria(new Criteria("withdrawCount").gte(min));
                } else if (max != null) {
                    query.addCriteria(new Criteria("withdrawCount").lte(max));
                }
            }
            Account account = memberCondition.getAccount();
            if (account != null && StringUtils.isNotBlank(account.getName())) {
                if (AccountType.parse(account.getType()) == AccountType.agent) {
                    query.addCriteria(new Criteria("agentName").is(account.getName()));
                }
                if (AccountType.parse(account.getType()) == AccountType.member) {
                    query.addCriteria(new Criteria("memberName").is(account.getName()));
                }
            }

        }
        return query;
    }

    public boolean updateLevel(long memberId, long level) {
        Query query = new Query(new Criteria("memberId").is(memberId));
        Update update = new Update().set("level", level);
        return super.update(query, update) != null;
    }

    public long getDepositMembers(Long agentId) {
        Query query = new Query();
        query.addCriteria(new Criteria("agentId").is(agentId)).
                addCriteria(new Criteria("depositCount").gt(0));
        return count(query);
    }

    /**
     * 批量获取代理储值会员数
     *
     * @param agentIds
     * @return
     */
    public Map<Long, Integer> batchGetDepositMembers(Collection<Long> agentIds){
        Map<Long, Integer> result = new HashMap<>();
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.project("agentId", "members", "depositCount"),
                Aggregation.match(Criteria.where("agentId").in(agentIds).and("depositCount").gt(0)),
                Aggregation.group("agentId").count().as("members"));
        AggregationResults<AgentDepositMember> aggregates = mongoTemplate.aggregate(aggregation, "memberConditionVo", AgentDepositMember.class);
        Iterator<AgentDepositMember> iterator = aggregates.iterator();
        while (iterator.hasNext()){
            AgentDepositMember next = iterator.next();
            result.put(next.getId(), next.getMembers());
        }
        return result;
    }

    /**
     * 批量获取会员的取款次数
     * @param members
     * @return
     */
    public Map<Long,Integer> getMemberWithdrawCount(List<Long> members) {
        if(members == null || members.size() < 1) return null;
        Map<Long, Integer> result = new HashMap<>();
        Query query = new Query().addCriteria(new Criteria("memberId").in(members));

        List<MemberConditionVo> list = super.find(query);
        ApiLogger.info(String.format("get data from mongo. query: %s, result: %s", JSON.toJSONString(query), JSON.toJSONString(list)));

        if(list != null){
            for (MemberConditionVo m:list) {
                result.put(m.getMemberId(),m.getWithdrawCount());
            }
        }

        return result;
    }

    /**
     * 批量获取会员数
     *
     * @param accounts
     * @param ownerId
     * @return
     */
    public List<MemberConditionVo> batchGetMembers(Collection<String> accounts, Long ownerId){
        Query query = new Query();
        query.addCriteria(new Criteria("ownerId").is(ownerId));
        query.addCriteria(new Criteria("memberName").in(accounts));
        return super.find(query);
    }
}
