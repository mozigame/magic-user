package com.magic.user.storage;

import com.magic.user.entity.Member;

/**
 * User: jack
 * Date: 2017/6/13
 * Time: 21:28
 */
public interface MemberRedisStorageService{

    /**
     * 刷新验证码
     *
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

    /**
     * 获取会员信息
     * @param memberId
     * @return
     */
    Member getMember(Long memberId);

    /**
     * 查询会员详情
     * @param member
     * @return
     */
    boolean setMember(Member member);

    /**
     * 删除会员详情
     * @param member
     * @return
     */
    boolean delsetMember(Long memberId);
}
