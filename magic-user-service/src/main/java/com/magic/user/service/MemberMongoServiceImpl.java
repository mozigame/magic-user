package com.magic.user.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.user.bean.MemberCondition;
import com.magic.user.dao.MemberMongoDaoImpl;
import com.magic.user.dao.OnlineMemberDaoImpl;
import com.magic.user.entity.Member;
import com.magic.user.entity.OnlineMemberConditon;
import com.magic.user.po.OnLineMember;
import com.magic.user.vo.MemberConditionVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * MemberMongoServiceImpl
 *
 * @author zj
 * @date 2017/5/11
 */
@Service("memberMongoService")
public class MemberMongoServiceImpl implements MemberMongoService {

    @Resource
    private OnlineMemberDaoImpl onlineMemberDao;
    @Resource
    private MemberMongoDaoImpl memberMongoDao;

    /**
     * {@inheritDoc}
     */
    @Override
    public long getOnlineMemberCount(OnlineMemberConditon memberCondition) {
        try {
            return onlineMemberDao.count(memberCondition);
        } catch (Exception e) {
            ApiLogger.error(String.format("get online member count error. condition: %s", JSON.toJSONString(memberCondition)), e);
        }
        return 0L;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OnLineMember> getOnlineMembers(OnlineMemberConditon memberCondition, Integer page, Integer count) {
        try {
            return onlineMemberDao.find(memberCondition, page, count);
        } catch (Exception e) {
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
        } catch (Exception e) {
            ApiLogger.error(String.format("save online member error. member: %s", JSON.toJSONString(member)), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OnLineMember getOnlineMember(Long memberId) {
        try {
            return onlineMemberDao.findById(memberId);
        } catch (Exception e) {
            ApiLogger.error(String.format("get onlineMember error.memberId: %d",memberId), e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateLogin(Long memberId, String ip, Long loginTime) {
        try {
            return onlineMemberDao.updateStatus(memberId, new Integer(1), new Integer(2), loginTime, ip);
        } catch (Exception e) {
            ApiLogger.error(String.format("update online member login error. memberId: %d, ip: %s, loginTime: %d", memberId, ip, loginTime), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateLogout(Long memberId) {
        try {
            return onlineMemberDao.updateStatus(memberId, new Integer(2), new Integer(1), null, null);
        } catch (Exception e) {
            ApiLogger.error(String.format("update online member logout error. memberId: %d", memberId), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * @param memberConditionVo
     * @return
     */
    @Override
    public boolean saveMemberInfo(MemberConditionVo memberConditionVo) {
        try {
            return memberMongoDao.save(memberConditionVo) != null;
        } catch (Exception e) {
            ApiLogger.error(String.format("save memberConditionVo error.memberId:%d", memberConditionVo.getMemberId()), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * @param memberId
     * @param status
     * @return
     */
    @Override
    public boolean updateMemberStatus(Long memberId, Integer status) {
        try {
            return memberMongoDao.updateMemberStatus(memberId,status);
        } catch (Exception e) {
            ApiLogger.error(String.format("update memberConditionVo error.memberId:%d, status:%d",memberId,status), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * @param memberId
     * @return
     */
    @Override
    public MemberConditionVo get(Long memberId) {
        try {
            return memberMongoDao.get(memberId);
        } catch (Exception e) {
            ApiLogger.error(String.format("get memberConditionVo error.memberId:%d ",memberId), e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @param memberCondition
     * @param page
     * @param count
     * @return
     */
    @Override
    public List<MemberConditionVo> queryByPage(MemberCondition memberCondition, Integer page, Integer count) {
        try {
            return memberMongoDao.queryByPage(memberCondition,page,count);
        } catch (Exception e) {
            ApiLogger.error(String.format("queryPage memberConditionVo error.condition:%d ", JSONObject.toJSONString(memberCondition)), e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @param memberCondition
     * @return
     */
    @Override
    public long getCount(MemberCondition memberCondition) {
        try {
            return memberMongoDao.getCount(memberCondition);
        } catch (Exception e) {
            ApiLogger.error(String.format("get memberConditionVo count error.condition:%d ", JSONObject.toJSONString(memberCondition)), e);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateLevel(Member member, long level) {
        try {
            return memberMongoDao.updateLevel(member.getMemberId(), level);
        }catch (Exception e){
            ApiLogger.error(String.format("update member level failed memberId:%d, level:%d ", member.getMemberId(),level), e);
        }
        return false;
    }

    @Override
    public long getDepositMembers(Long agentId) {
        try {
            return memberMongoDao.getDepositMembers(agentId);
        }catch (Exception e){
            ApiLogger.error(String.format("get memberConditionVo count agentId:%d ", agentId), e);
        }
        return 0L;
    }

    @Override
    public Map<Long, Integer> countDepositMembers(List<Long> agentIds) {
        try {
            return memberMongoDao.batchGetDepositMembers(agentIds);
        }catch (Exception e){
            ApiLogger.error(String.format("get memberConditionVo count deposit members, agentIds:%d ", agentIds), e);
        }
        return null;
    }
}
