package com.magic.user.service;

import com.alibaba.fastjson.JSON;
import com.magic.api.commons.ApiLogger;
import com.magic.user.dao.OnlineMemberDaoImpl;
import com.magic.user.entity.OnlineMemberConditon;
import com.magic.user.po.OnLineMember;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * MemberMongoServiceImpl
 *
 * @author zj
 * @date 2017/5/11
 */
@Service("memberMongoService")
public class MemberMongoServiceImpl implements MemberMongoService{

    @Resource
    private OnlineMemberDaoImpl onlineMemberDao;

    /**
     * {@inheritDoc}
     */
    @Override
    public long getOnlineMemberCount(OnlineMemberConditon memberCondition) {
        try {
            return onlineMemberDao.count(memberCondition);
        }catch (Exception e){
            ApiLogger.error(String.format("get online member count error. condition: %s", JSON.toJSONString(memberCondition)), e);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OnLineMember> getOnlineMembers(OnlineMemberConditon memberCondition, Integer page, Integer count) {
        try {
            return onlineMemberDao.find(memberCondition, page, count);
        }catch (Exception e){
            ApiLogger.error(String.format("get online member list error. condition: %s", JSON.toJSONString(memberCondition)));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean saveOnlieMember(OnLineMember member) {
        try {
            return onlineMemberDao.save(member) != null;
        }catch (Exception e){
            ApiLogger.error(String.format("save online member error. member: %s", JSON.toJSONString(member)), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateLogin(long memberId, String ip, long loginTime) {
        try {
            return onlineMemberDao.updateStatus(memberId, new Integer(1), new Integer(2), loginTime, ip);
        }catch (Exception e){
            ApiLogger.error(String.format("update online member login error. memberId: %d, ip: %s, loginTime: %d", memberId, ip, loginTime), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateLogout(long memberId) {
        try {
            return onlineMemberDao.updateStatus(memberId, new Integer(2), new Integer(1), null, null);
        }catch (Exception e){
            ApiLogger.error(String.format("update online member logout error. memberId: %d", memberId), e);
        }
        return false;
    }
}
