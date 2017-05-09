package com.magic.user.service;

import com.magic.api.commons.model.Page;
import com.magic.user.entity.User;
import com.magic.user.storage.UserDbService;
import com.magic.user.storage.UserMongoService;
import com.magic.user.util.UserVo;
import com.magic.user.vo.UserCondition;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/6
 * Time: 17:14
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource(name = "userDbService")
    private UserDbService userDbService;
    @Resource(name = "userMongoService")
    private UserMongoService userMongoService;


    @Override
    public int addUser(User user) {
        UserVo userVo = UserVo.parseUserVo(user);
        return userMongoService.insert(userVo);
    }

    @Override
    public int updateUser(User user) {
        UserVo userVo = UserVo.parseUserVo(user);
        return userMongoService.update(userVo);
    }

    @Override
    public Page<User> findByPage(UserCondition condition) {
        return null;
    }
}
