package com.magic.user.storage.impl;

import com.alibaba.fastjson.JSON;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.codis.JedisFactory;
import com.magic.user.constants.RedisConstants;
import com.magic.user.entity.Member;
import com.magic.user.storage.CountRedisStorageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import javax.annotation.Resource;
import java.util.*;

/**
 * CountRedisStorageServiceImpl
 *
 * @author zj
 * @date 2017/5/29
 */
@Service
public class CountRedisStorageServiceImpl implements CountRedisStorageService{

    @Resource(name = "permJedisFactory")
    private JedisFactory jedisFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean incrStock(Long ownerId) {
        try {
            String key = RedisConstants.assembleOwnerStockNum(ownerId);
            return jedisFactory.getInstance().incr(key) > 0;
        }catch (Exception e){
            ApiLogger.error(String.format("incr stocks of owner error. ownerId: %d", ownerId), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean incrAgent(Long ownerId) {
        try {
            String key = RedisConstants.assembleOwnerAgentNum(ownerId);
            return jedisFactory.getInstance().incr(key) > 0;
        }catch (Exception e){
            ApiLogger.error(String.format("incr agents of owner error. ownerId: %d", ownerId), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean incrWorker(Long ownerId) {
        try {
            String key = RedisConstants.assembleOwnerWorkerNum(ownerId);
            return jedisFactory.getInstance().incr(key) > 0;
        }catch (Exception e){
            ApiLogger.error(String.format("incr workers of owner error. ownerId: %d", ownerId), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean incrMember(Long ownerId) {
        try {
            String key = RedisConstants.assembleOwnerMemberNum(ownerId);
            return jedisFactory.getInstance().incr(key) > 0;
        }catch (Exception e){
            ApiLogger.error(String.format("incr members of owner error. ownerId: %d", ownerId), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStocks(Long ownerId) {
        try {
            String key = RedisConstants.assembleOwnerStockNum(ownerId);
            String value = jedisFactory.getInstance().get(key);
            if (StringUtils.isNotEmpty(value)) {
                return Integer.parseInt(value);
            }
        }catch (Exception e){
            ApiLogger.error(String.format("get stocks of owner error. ownerId: %d", ownerId), e);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAgents(Long ownerId) {
        try {
            String key = RedisConstants.assembleOwnerAgentNum(ownerId);
            String value = jedisFactory.getInstance().get(key);
            if (StringUtils.isNotEmpty(value)) {
                return Integer.parseInt(value);
            }
        }catch (Exception e){
            ApiLogger.error(String.format("get agents of owner error. ownerId: %d", ownerId), e);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMembers(Long ownerId) {
        try {
            String key = RedisConstants.assembleOwnerMemberNum(ownerId);
            String value = jedisFactory.getInstance().get(key);
            if (StringUtils.isNotEmpty(value)) {
                return Integer.parseInt(value);
            }
        }catch (Exception e){
            ApiLogger.error(String.format("get members of owner error. ownerId: %d", ownerId), e);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWorkers(Long ownerId) {
        try {
            String key = RedisConstants.assembleOwnerWorkerNum(ownerId);
            String value = jedisFactory.getInstance().get(key);
            if (StringUtils.isNotEmpty(value)){
                return Integer.parseInt(value);
            }
        }catch (Exception e){
            ApiLogger.error(String.format("get workers of owner error. ownerId: %d", ownerId), e);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long incr(String key) {
        try {
            return jedisFactory.getInstance().incr(key);
        }catch (Exception e){
            ApiLogger.error(String.format("incr key error. key: %s", key), e);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long decr(String key) {
        try {
            return jedisFactory.getInstance().decr(key);
        }catch (Exception e){
            ApiLogger.error(String.format("decr key error. key: %s", key), e);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean countLogins(Member member, String date) {
        try {
            int expireTime = 86400;//过期时间  1天 24 * 3600
            long memberId = member.getMemberId();
            long ownerId = member.getOwnerId();
            long agentId = member.getAgentId();
            Pipeline pipeline = jedisFactory.getInstance().pipelined();
            pipeline.expire(RedisConstants.assmbleIncrKey(memberId, date), expireTime);
            pipeline.hincrBy(RedisConstants.assmbleOwnerCountKey(date), String.valueOf(ownerId), 1);
            pipeline.hincrBy(RedisConstants.assmbleAgentCountKey(date), String.valueOf(agentId), 1);
            pipeline.sync();
            return true;
        }catch (Exception e){
            ApiLogger.error(String.format("count logins error. member: %s, data: %s", JSON.toJSONString(member), date), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCount(String key, Long uid) {
        try {
            String value = jedisFactory.getInstance().hget(key, String.valueOf(uid));
            if (StringUtils.isNotEmpty(value)) {
                return Long.parseLong(value);
            }
        }catch (Exception e){
            ApiLogger.error(String.format("get count from redis error. key: %s, uid: %d", key, uid), e);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCounts(String key) {
        long num = 0;
        Map<String, String> map;
        try {
            map = jedisFactory.getInstance().hgetAll(key);
        } catch (Exception e){
            ApiLogger.error(String.format("get counts from redis error. key: %s", key), e);
            return num;
        }
        if (Optional.ofNullable(map).filter(size -> map.size() > 0).isPresent()){
            Collection<String> values = map.values();
            Iterator<String> iterator = values.iterator();
            while (iterator.hasNext()){
                String value = iterator.next();
                try {
                    num += Long.parseLong(value);
                }catch (Exception e){
                   ApiLogger.error(String.format("parse str to long error.value: %s", value), e);
                }
            }
        }
        return num;
    }

    @Override
    public void addRegisterIpCount(String ip) {
        String key = RedisConstants.assembleCheckRegisterIp(ip);
        Jedis jedis = jedisFactory.getInstance();
        Long incr = jedis.incr(key);
        if (incr != null && incr < 2) {
            jedis.expire(key, 86400);
        }
    }

    @Override
    public long getRegisterIpCount(String ip) {
        try {
            String key = RedisConstants.assembleCheckRegisterIp(ip);
            String result = jedisFactory.getInstance().get(key);
            ApiLogger.info("getRegisterIpCount resut : " +result);
            if (StringUtils.isNotBlank(result)) {
                return Long.parseLong(result);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    @Override
    public void setPeriodLoginCount(Long ownerId, String userName, String ip) {
        String key = RedisConstants.assembleLoginCount(ownerId, userName, ip);
        Jedis jedis = jedisFactory.getInstance();
        Long incr = jedis.incr(key);
        if (incr != null && incr < 2) {
            jedis.expire(key, 900);
        }
    }

    @Override
    public long getPeriodLoginCount(Long ownerId, String userName, String ip) {
        try {
            String key = RedisConstants.assembleLoginCount(ownerId, userName, ip);
            String result = jedisFactory.getInstance().get(key);
            ApiLogger.info("getPeriodLoginCount result : " +result);
            if (StringUtils.isNotBlank(result)) {
                return Long.parseLong(result);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    @Override
    public boolean delPeriodLoginCount(Long ownerId, String userName, String ip) {
        try {
            String key = RedisConstants.assembleLoginCount(ownerId, userName, ip);
            Long result = jedisFactory.getInstance().del(key);
            return result != null && result > 0;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }
}
