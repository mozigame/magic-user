package com.magic.user.service.dubbo;

import com.magic.user.entity.Member;
import com.magic.user.entity.User;
import com.magic.user.enums.MemberStatus;
import com.magic.user.po.OwnerStaticInfo;
import com.magic.user.vo.MemberConditionVo;
import com.magic.user.vo.MemberInfoVo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * AccountDubboService
 *
 * @author zj
 * @date 2017/5/15
 */
public interface AccountDubboService {

    /**
     * 获取业主ID
     * @param uid 股东或代理ID
     * @return
     */
    long getOwnerId(long uid);

    /**
     * 获取用户
     * @param uid 股东或代理ID
     * @return
     */
    User getUser(long uid);

    /**
     * 获取会员
     * @param uid
     * @return
     */
    Member getMember(long uid);

    /**
     * 批量获取会员的取款次数
     * @param members
     * @return
     */
    Map<Long,Integer> getMemberWithdrawCount(List<Long> members);

    /**
     * 获取业主下股东、代理、会员、子账号数
     *
     * @param ownerIds
     * @return
     */
    Map<Long, OwnerStaticInfo> getOwnerStaticInfo(Collection<Long> ownerIds);

    /**
     * 检查uid是否登录，是否禁用
     * @param uid
     * @return
     */
    Member checkMemberLogin(long uid);

    /**
     *  获取会员的股东ID，业主ID及层级信息
     * @param memberId
     * @return
     */
    MemberInfoVo getMemberInfo(long memberId);

    /**
     * 获取一定周期内的代理列表
     * @param startTime
     * @param endTime
     * @param ownerId
     * @return
     */
    List<User> periodAgentList(Long startTime, Long endTime, Long ownerId);
    
    public List<User> getAgentListByPage(Integer offset, Integer count);

    /**
     * 验证会员登陆和启用情况
     *
     * @param uid
     * @return
     */
    MemberStatus verifyMember(long uid);
}
