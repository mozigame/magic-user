package com.magic.user.storage;

import com.magic.user.entity.User;

/**
 * User: joey
 * Date: 2017/5/15
 * Time: 11:07
 */
public interface UserRedisStorageService {


    boolean addUser(User user);

    boolean delUser(Long userId);

    boolean updateUser(User user);

    User getUser(Long userId);


}
