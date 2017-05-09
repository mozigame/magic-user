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
     * 获取用户数据
     * @param uid
     * @return
     */
    User getUserById(long uid);

    /**
     * 用户更新
     * @param id
     * @param realname
     * @param telephone
     * @param email
     * @param bankCardNo
     * @param bank
     * @param bankDeposit
     * @return
     */
    boolean updateUser(long id, String realname, String telephone, String email, String bankCardNo, String bank, String bankDeposit);

    /**
     * @param id
     * @return
     * @Doc 根据股东id获取业主id
     */
    long getOwnerIdByStock(long id);
}
