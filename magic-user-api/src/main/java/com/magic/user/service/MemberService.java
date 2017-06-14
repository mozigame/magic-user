package com.magic.user.service;

import com.magic.user.entity.Member;
import com.magic.user.enums.AccountStatus;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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
    List<Member> findMemberByIds(Collection<Long> ids);

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

    /**
     * 保存验证码
     * @param clientId
     * @param code
     * @return
     */
    boolean refreshCode(String clientId, String code);

    /**
     * 获取验证码
     *
     * @param clientId
     * @return
     */
    String getVerifyCode(String clientId);
}
