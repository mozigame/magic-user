package com.magic.user.service;

import com.magic.api.commons.model.Page;
import com.magic.user.entity.User;
import com.magic.user.vo.UserCondition;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:21
 */
public interface MemberService {

    Page<User> findByPage(UserCondition userCondition);
}
