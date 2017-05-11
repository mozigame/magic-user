package com.magic.user.service;

import com.magic.user.entity.Login;
import com.magic.user.entity.User;
import com.magic.user.vo.UserCondition;

import java.util.List;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 20:14
 */
public interface AgentService {

    List<Map<String, Object>> findByPage(UserCondition userCondition);

    long getCount(UserCondition userCondition);

    long add(User user);
}
