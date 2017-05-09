package com.magic.user.service;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:25
 */
public interface LoginService {

    /**
     * 修改登录密码
     *
     * @param id 用户ID
     * @param loginPassword 登陆密码
     * @return
     */
    boolean resetPassword(long id, String loginPassword);
}
