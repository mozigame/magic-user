package com.magic.user.service;

import com.magic.api.commons.model.Page;
import com.magic.user.entity.User;
import com.magic.user.vo.UserCondition;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:21
 */
public interface UserService {

    int addUser(User user);

    int updateUser(User user);

    Page<User> findByPage(UserCondition userCondition);

    /**
     * @param id
     * @return
     * @Doc 根据股东id获取业主id
     */
    long getOwnerIdByStock(long id);
}
