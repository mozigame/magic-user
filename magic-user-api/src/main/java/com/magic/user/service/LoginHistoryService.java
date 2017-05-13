package com.magic.user.service;

import com.magic.user.entity.Login;
import com.magic.user.entity.LoginHistory;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:22
 */
public interface LoginHistoryService {

    /**
     * @param login
     * @return
     * @Doc 添加登陆历史记录
     */
    boolean add(LoginHistory login);


}
