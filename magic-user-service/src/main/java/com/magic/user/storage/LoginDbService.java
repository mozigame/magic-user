package com.magic.user.storage;

import com.magic.api.commons.atlas.core.BaseDao;
import com.magic.user.dao.LoginDao;
import com.magic.user.entity.Login;
import com.magic.user.entity.User;
import com.magic.user.storage.base.BaseDbServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:39
 */
@Service("loginDbService")
public class LoginDbService extends BaseDbServiceImpl<Login, Long> {

    @Resource
    private LoginDao loginDao;

    @Override
    public BaseDao<Login, Long> getDao() {
        return loginDao;
    }
}
