package com.magic.user.service.dubbo;

import com.magic.user.service.StatisticsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * StatisticsDubboServiceImpl
 *
 * @author zj
 * @date 2017/5/31
 */
@Service("statisticsDubboService")
public class StatisticsDubboServiceImpl implements StatisticsDubboService{

    @Resource
    private StatisticsService statisticsService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Long> statisActiveMembers(long startTime, long endTime) {
        return statisticsService.getCounts(startTime, endTime);
    }
}
