package com.magic.user.service;

import com.magic.user.entity.Member;
import com.magic.user.enums.AccountStatus;

import java.util.List;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:21
 */
public interface MemberService {
    /**
     * 查询会员数据
     * @param id 会员ID
     * @return
     */
    Member getMemberById(Long id);

    /**
     * 查询多个会员数据
     * @param ids 会员ID
     * @return
     */
    List<Member> findMemberByIds(List<Long> ids);

    /**
     * 更新会员数据
     * @param member
     * @return
     */
    boolean updateMember(Member member);

    /**
     * 更新会员状态
     *
     * @return
     */
    boolean updateStatus(Member member);

    /**
     * 保存会员数据
     * @param member
     * @return
     */
    boolean saveMember(Member member);
}
