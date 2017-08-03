package com.magic.user.dao;

import com.magic.user.entity.OnlineMemberConditon;
import com.magic.user.po.OnLineMember;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OnlineMemberDaoImpl
 *
 * @author zj
 * @date 2017/5/10
 */
@Service("onlineMemberDao")
public class OnlineMemberDaoImpl extends BaseMongoDAOImpl<OnLineMember>{

    /**
     * 更新mongodb 在线会员记录状态，登录时间，登录ip
     *
     * @param memberId
     * @param oldStatus
     * @param newStatus
     * @param loginTime
     * @param loginIp
     * @return
     */
    public boolean updateStatus(long memberId, Integer oldStatus, Integer newStatus, Long loginTime, String loginIp){
        Query query = new Query(new Criteria("memberId").is(memberId));
        if (oldStatus != null && loginTime > 0){
            query.addCriteria(new Criteria("status").is(oldStatus));
        }
        Update update = new Update();
        if (newStatus != null && newStatus > 0){
            update.set("status", newStatus);
        }
        if (loginTime != null && loginTime > 0){
            update.set("loginTime", loginTime);
        }
        if (StringUtils.isNoneEmpty(loginIp)){
            update.set("loginIp", loginIp);
        }
        return super.update(query, update) != null;
    }

    /**
     * 根据会员ID查询会员在线记录
     *
     * @param memberId
     * @return
     */
    public OnLineMember findById(long memberId) {
        return super.findOne(new Query(new Criteria("memberId").is(memberId)).addCriteria(new Criteria("status").is(2)));
    }

    /**
     * 根据会员ID查询会员在线记录
     *
     * @param account
     * @return
     */
    public OnLineMember findByAccount(String account) {
        return super.findOne(new Query(new Criteria("account").is(account)));
    }

    /**
     * 翻页查询
     *
     * @param condition
     * @param page
     * @param size
     * @return
     */
    public List<OnLineMember> find(OnlineMemberConditon condition, Integer page, Integer size){
        Query query = parseQuery(condition);
        if(page != null && page > 0){
            query.skip((page-1) * size).limit(size);
        }
        //组装排序
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "loginTime")));
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, "registerTime")));
        return super.find(query);
    }

    /**
     * 总条目数
     *
     * @param conditon
     * @return
     */
    public long count(OnlineMemberConditon conditon){
        Query query = parseQuery(conditon);
        return super.count(query);
    }

    /**
     * 解析查询条件
     * @param condition
     * @return
     */
    private Query parseQuery(OnlineMemberConditon condition){
        Query query = new Query();
        //组装查询条件
        if (condition != null){
            Long memberId = condition.getMemberId();
            if (memberId != null && memberId > 0){
                query.addCriteria(new Criteria("memberId").is(memberId));
            }
            Long agentId = condition.getAgentId();
            if (agentId != null && agentId > 0){
                query.addCriteria(new Criteria("agentId").is(agentId));
            }
            Long holderId = condition.getHolderId();
            if (holderId != null && holderId > 0){
                query.addCriteria(new Criteria("holderId").is(holderId));
            }
            Long ownerId = condition.getOwnerId();
            if (ownerId != null && ownerId > 0){
                query.addCriteria(new Criteria("ownerId").is(ownerId));
            }
            Integer status = condition.getStatus();
            if (status != null && status > 0){
                query.addCriteria(new Criteria("status").is(status));
            }
            Long loginStartTime = condition.getLoginStartTime();
            Long loginEndTime = condition.getLoginEndTime();
            if (loginStartTime != null && loginStartTime > 0 && loginEndTime != null && loginEndTime > 0) {
                query.addCriteria(new Criteria("loginTime").gte(loginStartTime).lte(loginEndTime));
            } else if (loginStartTime != null && loginStartTime > 0 ){
                query.addCriteria(new Criteria("loginTime").gte(loginStartTime));
            } else if (loginEndTime != null && loginEndTime > 0) {
                query.addCriteria(new Criteria("loginTime").lte(loginEndTime));
            }
            Long registerStartTime = condition.getRegisterStartTime();
            Long registerEndTime = condition.getRegisterEndTime();
            if (registerStartTime != null && registerStartTime > 0 && registerEndTime != null && registerEndTime > 0){
                query.addCriteria(new Criteria("registerTime").gte(registerStartTime).lte(registerEndTime));
            } else if (registerStartTime != null && registerStartTime > 0) {
                query.addCriteria(new Criteria("registerTime").gte(registerStartTime));
            } else if (registerEndTime != null && registerEndTime > 0) {
                query.addCriteria(new Criteria("registerTime").lte(registerEndTime));
            }
        }
        return query;
    }

    /**
     * 获取同一登录ip下的所有会员
     * @param ip
     * @return
     */
    public List<OnLineMember> getIpMembers(String ip, Integer page, Integer size) {
        Query query = new Query(new Criteria("loginIp").is(ip));
        if(page != null && page > 0){
            query.skip((page-1) * size).limit(size);
        }
        return super.find(query);
    }
    /**
     * 获取同一登录ip下的会员数量
     * @param ip
     * @return
     */
    public long getIpMembersCount(String ip) {
        Query query = new Query(new Criteria("loginIp").is(ip));
        return super.count(query);
    }
}
