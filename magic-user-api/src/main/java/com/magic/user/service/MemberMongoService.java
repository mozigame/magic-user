package com.magic.user.service;

import com.magic.user.bean.MemberCondition;
import com.magic.user.entity.Member;
import com.magic.user.entity.OnlineMemberConditon;
import com.magic.user.po.OnLineMember;
import com.magic.user.vo.MemberConditionVo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * MemberMongoService
 *
 * @author zj
 * @date 2017/5/11
 */
public interface MemberMongoService {

    /**
     * 统计在线人数
     * @param memberCondition
     * @return
     */
    long getOnlineMemberCount(OnlineMemberConditon memberCondition);

    /**
     * 在线会员列表
     * @param memberCondition
     * @return
     */
    List<OnLineMember> getOnlineMembers(OnlineMemberConditon memberCondition, Integer page, Integer count);

    /**
     * 保存在线会员数
     * @param lineMember
     * @return
     */
    boolean saveOnlieMember(OnLineMember lineMember);

    /**
     * 查询在线会员数
     *
     * @param memberId
     * @return
     */
    OnLineMember getOnlineMember(Long memberId);

    /**
     * 修改在线会员状态，ip，登录时间
     * @param memberId
     * @param ip
     * @param loginTime
     * @return
     */
    boolean updateLogin(Long memberId, String ip, Long loginTime);

    /**
     * 注销，修改会员状态
     * @param memberId
     * @return
     */
    boolean updateLogout(Long memberId);

    /**
     * @Doc 添加会员数据
     * @param memberMongoVo
     * @return
     */
    boolean saveMemberInfo(MemberConditionVo memberMongoVo);

    /**
     * @Doc 修改会员可用状态
     * @param memberId
     * @param status
     * @return
     */
    boolean updateMemberStatus(Long memberId,Integer status);

    /**
     * @Doc 获取会员信息
     * @param memberId
     * @return
     */
    MemberConditionVo get(Long memberId);

    /**
     * @Doc 分页查询会员信息
     * @param memberCondition
     * @param page
     * @param count
     * @return
     */
    List<MemberConditionVo> queryByPage(MemberCondition memberCondition, Integer page, Integer count);

    /**
     * @Doc 获取会员总数
     * @param memberCondition
     * @return
     */
    long getCount(MemberCondition memberCondition);

    /**
     * @Doc 更新会员层级
     * @param member
     * @param level
     * @return
     */
    boolean updateLevel(Member member, long level);

    /**
     * @Doc 根据代理Id查询存款会员数量
     * @param agentId
     * @return
     */
    long getDepositMembers(Long agentId);

    /**
     *  获取代理的存款会员数量
     * @param agentIds
     * @return
     */
    Map<Long,Integer> countDepositMembers(List<Long> agentIds);

    /**
     * 批量获取会员的取款次数
     * @param members
     * @return
     */
    Map<Long,Integer> getMemberWithdrawCount(List<Long> members);

    /**
     * 批量获取会员数据
     * @param accounts
     * @param ownerId
     * @return
     */
    List<MemberConditionVo> batchQuery(Collection<String> accounts, Long ownerId);

    /**
     * 更新mongo数据
     *
     * @param memberId
     * @param updateMap
     * @return
     */
    boolean updateMemberInfo(Number memberId, Map<String, Object> updateMap);
}
