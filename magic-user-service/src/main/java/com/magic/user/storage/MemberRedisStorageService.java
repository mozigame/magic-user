package com.magic.user.storage;

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
}
