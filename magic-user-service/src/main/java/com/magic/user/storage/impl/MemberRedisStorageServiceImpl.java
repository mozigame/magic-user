package com.magic.user.storage.impl;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.codis.JedisFactory;
import com.magic.api.commons.tools.IPUtil;
import com.magic.user.constants.RedisConstants;
import com.magic.user.constants.UserContants;
import com.magic.user.storage.MemberRedisStorageService;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Pipeline;

import javax.annotation.Resource;

/**
 * MemberRedisStorageServiceImpl
 *
 * @author zj
 * @date 2017/6/13
 */
@Service("memberRedisStorageService")
public class MemberRedisStorageServiceImpl implements MemberRedisStorageService{

    @Resource(name = "permJedisFactory")
    private JedisFactory jedisFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean refreshCode(long ip, String code) {
        try {
            Pipeline pipelined = jedisFactory.getInstance().pipelined();
            String key = RedisConstants.assembleVerifyCode(ip);
            pipelined.set(key, code);
            pipelined.expire(key, UserContants.EXPIRE_TIME);
            pipelined.sync();
            return true;
        }catch (Exception e){
            ApiLogger.error(String.format("refresh code error. ip: %d, code: %s", ip, code), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVerifyCode(String ip) {
        try {
            return jedisFactory.getInstance().get(RedisConstants.assembleVerifyCode(IPUtil.ipToLong(ip)));
        }catch (Exception e){
            ApiLogger.error(String.format("get verify code from redis error. ip: %s", ip), e);
        }
        return null;
    }
}
