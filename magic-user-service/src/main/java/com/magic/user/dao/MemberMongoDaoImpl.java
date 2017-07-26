package com.magic.user.dao;

import com.alibaba.fastjson.JSON;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.utils.StringUtils;
import com.magic.user.bean.Account;
import com.magic.user.bean.MemberCondition;
import com.magic.user.enums.AccountType;
import com.magic.user.vo.AgentDepositMember;
import com.magic.user.vo.MemberConditionVo;
import org.apache.commons.collections.CollectionUtils;
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
            ApiLogger.debug("memberCondition = " + memberCondition
             + ", query = " + query);
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
        formatQuery(memberCondition, query);
        return query;
    }

    private void formatQuery(MemberCondition memberCondition, Query query) {
        if (memberCondition != null) {
            query.addCriteria(new Criteria("ownerId").is(memberCondition.getOwnerId()));
            if (memberCondition.getAgentId() != null) {
                query.addCriteria(new Criteria("agentId").is(memberCondition.getAgentId()));
            }
            disposeRegisterTime(memberCondition, query);
            disposeDepositMoney(memberCondition, query);
            disposeWithdrawMoney(memberCondition, query);

            if (memberCondition.getStatus() != null) {
                query.addCriteria(new Criteria("status").is(memberCondition.getStatus()));
            }
            disposeLevel(memberCondition, query);
            disposeDepositCount(memberCondition, query);
            disposeWithdrawCount(memberCondition, query);

            disposeAccount(memberCondition, query);
        }
    }

    private void disposeWithdrawCount(MemberCondition memberCondition, Query query) {
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
    }

    private void disposeDepositCount(MemberCondition memberCondition, Query query) {
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
    }

    private void disposeLevel(MemberCondition memberCondition, Query query) {
        //优先list
        if (CollectionUtils.isNotEmpty(memberCondition.getLevelList())) {
            query.addCriteria(new Criteria("level").in(memberCondition.getLevelList()));
        } else {
            if (memberCondition.getLevel() != null) {
                query.addCriteria(new Criteria("level").is(memberCondition.getLevel()));
            }
        }
    }

    private void disposeWithdrawMoney(MemberCondition memberCondition, Query query) {
        if (memberCondition.getWithdrawMoney() != null) {
            Long min = memberCondition.getWithdrawMoney().getMin();
            Long max = memberCondition.getWithdrawMoney().getMax();
            if (min != null && max != null) {
                query.addCriteria(new Criteria("withdrawMoney").gte(min).lte(max));
            } else if (min != null) {
                query.addCriteria(new Criteria("withdrawMoney").gte(min));
            } else if (max != null) {
                query.addCriteria(new Criteria("withdrawMoney").lte(max));
            }
        }
    }

    private void disposeDepositMoney(MemberCondition memberCondition, Query query) {
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
    }

    private void disposeRegisterTime(MemberCondition memberCondition, Query query) {
        if (memberCondition.getRegister() != null) {
            Long start = memberCondition.getRegister().getStart();
            Long end = memberCondition.getRegister().getEnd();
            if (start != null && end != null) {
                query.addCriteria(new Criteria("registerTime").gte(start).lte(end));
            } else if (start != null) {
                query.addCriteria(new Criteria("registerTime").gte(start));
            } else if (end != null) {
                query.addCriteria(new Criteria("registerTime").lte(end));
            }
        }
    }

    private void disposeAccount(MemberCondition memberCondition, Query query) {
        //优先list
        if (CollectionUtils.isNotEmpty(memberCondition.getAccountList())) {
            List<String> agentNameList = new LinkedList<>();
            List<String> memberNameList = new LinkedList<>();
            List<Number> memberIdList = new LinkedList<>();
            List<Number> agentIdList = new LinkedList<>();
            for (Account account : memberCondition.getAccountList()) {
                if (account != null) {
                    if (StringUtils.isNotBlank(account.getName())) {
                        if (AccountType.parse(account.getType()) == AccountType.agent) {
                            agentNameList.add(account.getName());
                        } else if (AccountType.parse(account.getType()) == AccountType.member) {
                            memberNameList.add(account.getName());
                        }
                    }
                    if (account.getId() != null) {
                        if (AccountType.parse(account.getType()) == AccountType.agent) {
                            agentIdList.add(account.getId());
                        } else if (AccountType.parse(account.getType()) == AccountType.member) {
                            memberIdList.add(account.getId());
                        }
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(agentNameList)){
                query.addCriteria(new Criteria("agentName").in(agentNameList));
            }
            if (CollectionUtils.isNotEmpty(memberNameList)){
                query.addCriteria(new Criteria("memberName").in(memberNameList));
            }
            if (CollectionUtils.isNotEmpty(memberIdList)){
                query.addCriteria(new Criteria("memberId").in(memberIdList));
            }
            if (CollectionUtils.isNotEmpty(agentIdList)){
                query.addCriteria(new Criteria("agentId").in(agentIdList));
            }
        } else {
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
    public Map<Long, Integer> batchGetDepositMembers(Collection<Long> agentIds) {
        Map<Long, Integer> result = new HashMap<>();
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.project("agentId", "members", "depositCount"),
                Aggregation.match(Criteria.where("agentId").in(agentIds).and("depositCount").gt(0)),
                Aggregation.group("agentId").count().as("members"));
        AggregationResults<AgentDepositMember> aggregates = mongoTemplate.aggregate(aggregation, "memberConditionVo", AgentDepositMember.class);
        Iterator<AgentDepositMember> iterator = aggregates.iterator();
        while (iterator.hasNext()) {
            AgentDepositMember next = iterator.next();
            result.put(next.getId(), next.getMembers());
        }
        return result;
    }

    /**
     * 批量获取会员的取款次数
     *
     * @param members
     * @return
     */
    public Map<Long, Integer> getMemberWithdrawCount(List<Long> members) {
        if (members == null || members.size() < 1) return null;
        Map<Long, Integer> result = new HashMap<>();
        Query query = new Query().addCriteria(new Criteria("memberId").in(members));

        List<MemberConditionVo> list = super.find(query);
        ApiLogger.info(String.format("get data from mongo. query: %s, result: %s", JSON.toJSONString(query), JSON.toJSONString(list)));

        if (list != null) {
            for (MemberConditionVo m : list) {
                result.put(m.getMemberId(), m.getWithdrawCount());
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
    public List<MemberConditionVo> batchGetMembers(Collection<String> accounts, Long ownerId) {
        Query query = new Query();
        query.addCriteria(new Criteria("ownerId").is(ownerId));
        query.addCriteria(new Criteria("memberName").in(accounts));
        return super.find(query);
    }
}
