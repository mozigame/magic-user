package com.magic.user.service;

import com.magic.user.entity.Login;
import com.magic.user.entity.LoginHistory;
import com.magic.user.storage.LoginDbService;
import com.magic.user.storage.LoginHistoryDbService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/13
 * Time: 11:06
 */
@Service("loginHistoryService")
public class LoginHistoryServiceImpl implements LoginHistoryService {

    @Resource(name = "loginHistoryDbService")
    private LoginHistoryDbService loginHistoryDbService;

    @Override
    public boolean add(LoginHistory login) {
        Long count = loginHistoryDbService.insert(login);
        return (count == null || count <= 0) ? false : true;
    }
}
