package com.magic.user.member.resource.service;

import com.magic.api.commons.model.Page;
import com.magic.user.entity.Member;
import com.magic.user.entity.User;
import com.magic.user.vo.UserCondition;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 1:32
 */
public interface MemberResourceService {

    Page<User> findByPage(UserCondition userCondition);
}
