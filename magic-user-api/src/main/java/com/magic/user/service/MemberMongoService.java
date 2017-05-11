package com.magic.user.service;

import com.magic.user.entity.OnlineMemberConditon;
import com.magic.user.po.OnLineMember;

import java.util.List;

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
     * 修改在线会员状态，ip，登录时间
     * @param memberId
     * @param ip
     * @param loginTime
     * @return
     */
    boolean updateLogin(long memberId, String ip, long loginTime);

    /**
     * 注销，修改会员状态
     * @param memberId
     * @return
     */
    boolean updateLogout(long memberId);
}
