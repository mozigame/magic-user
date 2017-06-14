package com.magic.user.storage.impl;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.codis.JedisFactory;
import com.magic.api.commons.tools.IPUtil;
import com.magic.api.commons.utils.StringUtils;
import com.magic.user.constants.RedisConstants;
import com.magic.user.constants.UserContants;
import com.magic.user.storage.MemberRedisStorageService;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
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
    public boolean refreshCode(String clientId, String code) {
        try {
            Pipeline pipelined = jedisFactory.getInstance().pipelined();
            String key = RedisConstants.assembleVerifyCode(clientId);
            long expireTime = System.currentTimeMillis() + UserContants.VERIFY_CODE_VALID_TIME;
            String value = code.concat(UserContants.SPLIT_LINE).concat(String.valueOf(expireTime));
            pipelined.set(key, value);
            pipelined.expire(key, UserContants.EXPIRE_TIME);
            pipelined.sync();
            return true;
        }catch (Exception e){
            ApiLogger.error(String.format("refresh code error. clientId: %s, code: %s", clientId, code), e);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVerifyCode(String clientId) {
        try {
            Jedis instance = jedisFactory.getInstance();
            String key = RedisConstants.assembleVerifyCode(clientId);
            String value = instance.get(key);
            if (StringUtils.isNotEmpty(value)){
                instance.del(key);
            }
            return value;
        }catch (Exception e){
            ApiLogger.error(String.format("get verify code from redis error. clientId: %s", clientId), e);
        }
        return null;
    }
}
