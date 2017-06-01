package com.magic.user.service.dubbo;

import java.util.Map;
/**
 * StatisticsDubboService
 *
 * @author zj
 * @date 2017/5/31
 */
public interface StatisticsDubboService {

    /**
     * 统计指定条件下所有业主下的活跃用户数
     *
     * @param startTime
     * @param endTime
     * @return
     */
    Map<String, Long> statisActiveMembers(long startTime, long endTime);
}
