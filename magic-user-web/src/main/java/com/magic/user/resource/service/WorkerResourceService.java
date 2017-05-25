package com.magic.user.resource.service;

import com.magic.api.commons.core.context.RequestContext;

/**
 * User: joey
 * Date: 2017/5/24
 * Time: 18:39
 */
public interface WorkerResourceService {


    /**
     * @Doc 子账号列表
     * @param account
     * @param realname
     * @return
     */
    String list(RequestContext rc, String account, String realname, Integer page, Integer count);

    /**
     * @Doc 添加子账号
     * @param account
     * @param password
     * @param realname
     * @param roleId
     * @return
     */
    String add(RequestContext rc, String account,String password,String realname, Integer roleId);

    /**
     * 修改子账号信息
     * @param userId
     * @param realname
     * @param roleId
     * @return
     */
    String update(RequestContext rc, Long userId, String realname, Integer roleId);

    /**
     * 修改状态
     * @param userId
     * @param status
     * @return
     */
    String updateStatus(RequestContext rc, Long userId, Integer status);

    /**
     * 查询子账号详情
     * @param userId
     * @return
     */
    String detail(RequestContext rc, Long userId);

    /**
     * 重置密码
     * @param userId
     * @param password
     * @return
     */
    String pwdReset(RequestContext rc, Long userId, String password);
}
