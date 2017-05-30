package com.magic.user.storage.impl;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.codis.JedisFactory;
import com.magic.user.constants.RedisConstants;
import com.magic.user.storage.CountRedisStorageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
            return Integer.parseInt(jedisFactory.getInstance().get(key));
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
            return Integer.parseInt(jedisFactory.getInstance().get(key));
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
            return Integer.parseInt(jedisFactory.getInstance().get(key));
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
            return Integer.parseInt(jedisFactory.getInstance().get(key));
        }catch (Exception e){
            ApiLogger.error(String.format("get workers of owner error. ownerId: %d", ownerId), e);
        }
        return 0;
    }
}
