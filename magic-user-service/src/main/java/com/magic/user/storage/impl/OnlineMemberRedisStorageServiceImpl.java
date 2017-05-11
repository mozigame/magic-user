package com.magic.user.storage.impl;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.codis.JedisFactory;
import com.magic.user.constants.RedisConstants;
import com.magic.user.storage.OnlineMemberRedisStorageService;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;

/**
 * OnlineMemberRedisStorageServiceImpl
 *
 * @author zj
 * @date 2017/5/10
 */
@Service("onlineMemberRedisStorageService")
public class OnlineMemberRedisStorageServiceImpl implements OnlineMemberRedisStorageService{

    @Resource(name = "permJedisFactory")
    private JedisFactory jedisFactory;
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addOnlineMember(Long ownerId, Long memberId) {
        try {
            Jedis instance = jedisFactory.getInstance();
            String key = RedisConstants.assembleOwnerMembersList(ownerId);
            
        }catch (Exception e){
            ApiLogger.error(String.format("add online member to redis error.ownerId: %d, memberId: %d", ownerId, memberId), e);
        }
        return false;
    }
}
