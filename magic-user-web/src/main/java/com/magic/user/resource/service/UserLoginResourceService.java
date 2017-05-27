package com.magic.user.resource.service;

import com.magic.api.commons.core.context.RequestContext;

/**
 * User: joey
 * Date: 2017/5/27
 * Time: 15:05
 */
public interface UserLoginResourceService {


    /**
     * @param rc
     * @param agent
     * @param url
     * @param username
     * @param password
     * @param code
     * @return
     * @Doc 用户登录
     */
    String login(RequestContext rc, String agent, String url, String username, String password, String code);

    /**
     * @param rc
     * @param username
     * @return
     * @Doc 用户注销
     */
    String logout(RequestContext rc, String agent, String username);

    /**
     * 核实登录信息
     * @param rc
     * @return
     */
    String verify(RequestContext rc);
}
