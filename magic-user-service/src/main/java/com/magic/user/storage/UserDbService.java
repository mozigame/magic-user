package com.magic.user.storage;

import com.magic.api.commons.atlas.core.BaseDao;
import com.magic.user.dao.UserDao;
import com.magic.user.entity.User;
import com.magic.user.storage.base.BaseDbServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:28
 */
@Service("userDbService")
public class UserDbService extends BaseDbServiceImpl<User, Long> {

    @Resource
    private UserDao userDao;

    @Override
    public BaseDao<User, Long> getDao() {
        return userDao;
    }


}
