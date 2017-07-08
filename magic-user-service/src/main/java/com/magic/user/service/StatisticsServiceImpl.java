package com.magic.user.service;

import com.alibaba.fastjson.JSON;
import com.magic.api.commons.tools.LocalDateTimeUtil;
import com.magic.user.constants.RedisConstants;
import com.magic.user.entity.User;
import com.magic.user.enums.AccountType;
import com.magic.user.storage.CountRedisStorageService;
import com.magic.user.util.UserUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * StatisticsServiceImpl
 *
 * @author zj
 * @date 2017/5/31
 */
@Service
public class StatisticsServiceImpl implements StatisticsService{

    @Resource
    private CountRedisStorageService countRedisStorageService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Long> getCounts(User user, Set<String> dates) {
        Map<String, Long> result = new LinkedHashMap<>();
        AccountType type = user.getType();
        Long ownerId = user.getOwnerId();
        Long userId = user.getUserId();
        Iterator<String> iterator = dates.iterator();
        while (iterator.hasNext()){
            String date = iterator.next();
            long num = 0;
            if (type == AccountType.stockholder || type == AccountType.proprietor || type == AccountType.worker){
                num = countRedisStorageService.getCount(RedisConstants.assmbleOwnerCountKey(date), ownerId);
            }
            if (type == AccountType.agent){
                num = countRedisStorageService.getCount(RedisConstants.assmbleAgentCountKey(date), userId);
            }
            result.put(date, num);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Long> getCounts(long startTime, long endTime) {
        Set<String> dates = UserUtil.parseDates(startTime, endTime);
        Map<String, Long> result = new LinkedHashMap<>();
        Iterator<String> iterator = dates.iterator();
        while (iterator.hasNext()){
            String date = iterator.next();
            long num = countRedisStorageService.getCounts(RedisConstants.assmbleOwnerCountKey(date));
            result.put(date, num);
        }
        return result;
    }

}