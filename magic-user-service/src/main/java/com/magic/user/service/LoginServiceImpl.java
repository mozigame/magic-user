package com.magic.user.service;

import com.magic.user.entity.Login;
import com.magic.user.storage.LoginDbService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Long result = loginDbService.insert(login);
        return result == null ? 0 : result;
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

    @Override
    public Map<Long, Login> findByUserIds(List<Long> ids) {
        List<Login> list = loginDbService.find("selectByUserIds", new String[]{"list"},new Object[]{ids});
        if (list != null) {
            Map<Long, Login> loginMap = new HashMap<>();
            for (Login login : list) {
                loginMap.put(login.getUserId(), login);
            }
            return loginMap;
        }
        return null;
    }
}
