package com.magic.user.service;

import com.magic.user.entity.Login;

import java.util.List;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:25
 */
public interface LoginService {

    /**
     * @Doc 添加用户登录基础信息
     * @param login
     * @return
     */
    long add(Login login);

    /**
     * 修改登录密码
     *
     * @param id            用户ID
     * @param loginPassword 登陆密码
     * @return
     */
    boolean resetPassword(Long id, String loginPassword);

    /**
     * 根据用户id获取用户的登录信息
     *
     * @param id
     * @return
     */
    Login get(Long id);

    /**
     * @Doc 修改登陆状态
     * @param userId
     * @param lastLoginTime
     * @param lastLoginIp
     * @param status
     * @return
     */
    boolean updateLoginStatus(Long userId, Long lastLoginTime, Integer lastLoginIp, Integer status);

    /**
     * 批量获取用户登录信息
     * @return
     */
    Map<Long, Login> findByUserIds(List<Long> ids);
}
