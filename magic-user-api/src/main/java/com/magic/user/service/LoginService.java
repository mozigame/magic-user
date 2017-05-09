package com.magic.user.service;

import com.magic.user.entity.Login;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:25
 */
public interface LoginService {

    long add(Login login);

    long update(Login login);
}
