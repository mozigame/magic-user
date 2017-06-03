package com.magic.user.resource.service;

import com.magic.api.commons.core.context.RequestContext;

/**
 * StatisticsResourceService
 *
 * @author zj
 * @date 2017/5/31
 */
public interface StatisticsResourceService {
    /**
     * 统计业主/代理时间段内活跃用户数
     *
     * @param rc RequestContext
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    String statisMemberLogins(RequestContext rc, Long startTime, Long endTime);
}
