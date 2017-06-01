package com.magic.user.resource.service.impl;

import com.alibaba.fastjson.JSON;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.model.SimpleListResult;
import com.magic.user.constants.UserContants;
import com.magic.user.entity.User;
import com.magic.user.resource.service.StatisticsResourceService;
import com.magic.user.service.StatisticsService;
import com.magic.user.service.UserService;
import com.magic.user.util.UserUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * StatisticsResourceServiceImpl
 *
 * @author zj
 * @date 2017/5/31
 */
@Service
public class StatisticsResourceServiceImpl implements StatisticsResourceService{

    @Resource
    private UserService userService;
    @Resource
    private StatisticsService statisticsService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String statisMemberLogins(RequestContext rc, Long startTime, Long endTime) {
        long uid = rc.getUid();
        long cmillis = System.currentTimeMillis();
        if (!Optional.ofNullable(startTime).filter(start -> start > 0).isPresent()){
            startTime = cmillis;
        }
        if (!Optional.ofNullable(endTime).filter(end -> end > 0).isPresent()){
            endTime = cmillis;
        }
        if (startTime > endTime){
            return UserContants.EMPTY_STRING;
        }
        User user = userService.get(uid);
        if (!Optional.ofNullable(user).isPresent()){
            return UserContants.EMPTY_STRING;
        }
        Set<String> dates = UserUtil.parseDates(startTime, endTime);
        Map<String, Long> map = statisticsService.getCounts(user, dates);
        return JSON.toJSONString(map);
    }
}
