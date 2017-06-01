package com.magic.user.service;

import com.magic.user.entity.User;

import java.util.Map;
import java.util.Set;

/**
 * StatisticsService
 *
 * @author zj
 * @date 2017/5/31
 */
public interface StatisticsService {
    /**
     * 获取业主/代理时间段内的活跃会员数
     *
     * @param user
     * @param dates
     * @return
     */
    Map<String, Long> getCounts(User user, Set<String> dates);

    /**
     * 获取时间段内活跃会员数
     *
     * @param startTime
     * @param endTime
     * @return
     */
    Map<String,Long> getCounts(long startTime, long endTime);
}
