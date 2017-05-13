package com.magic.user.service;

import com.magic.user.entity.Login;
import com.magic.user.storage.LoginDbService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/9
 * Time: 19:42
 */
@Service("loginService")
public class LoginServiceImpl implements LoginService {

    @Resource(name = "loginDbService")
    private LoginDbService loginDbService;

    @Override
    public long add(Login login) {
        return loginDbService.insert(login);
    }

    @Override
    public int update(Login login) {
        return loginDbService.update("updatePwd", null, login);
    }

    @Override
    public boolean resetPassword(Long id, String loginPassword) {
        return loginDbService.update("updatePwd", new String[]{"id", "password"}, new Object[]{id, loginPassword}) > 0;
    }

    @Override
    public Login get(Long id) {
        return loginDbService.get(id);
    }

    @Override
    public boolean updateLoginStatus(Long userId, Long lastLoginTime, Integer lastLoginIp, Integer status) {
        return loginDbService.update("updateLoginStatus", new String[]{"userId", "lastLoginTime", "lastLoginIp", "status"}, new Object[]{userId, lastLoginTime, lastLoginIp, status}) > 0;
    }
}
