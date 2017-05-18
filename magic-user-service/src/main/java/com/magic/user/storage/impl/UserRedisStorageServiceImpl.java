package com.magic.user.storage.impl;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.codis.JedisFactory;
import com.magic.api.commons.utils.StringUtils;
import com.magic.user.constants.RedisConstants;
import com.magic.user.entity.User;
import com.magic.user.storage.UserRedisStorageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/15
 * Time: 11:08
 */
@Service("userRedisStorageService")
public class UserRedisStorageServiceImpl implements UserRedisStorageService {

    @Resource(name = "permJedisFactory")
    private JedisFactory jedisFactory;

    @Override
    public boolean addUser(User user) {
        try {
            String key = RedisConstants.USER_PREFIX.USER_BASE_INFO.key(user.getUserId());
            jedisFactory.getInstance().setex(key, RedisConstants.USER_PREFIX.USER_BASE_INFO.expire(), JSONObject.toJSONString(user));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delUser(Long userId) {
        try {
            String userKey = RedisConstants.USER_PREFIX.USER_BASE_INFO.key(userId);
            long result = jedisFactory.getInstance().del(userKey);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateUser(User user) {
        try {
            String userKey = RedisConstants.USER_PREFIX.USER_BASE_INFO.key(user.getUserId());
            jedisFactory.getInstance().setex(userKey, RedisConstants.USER_PREFIX.USER_BASE_INFO.expire(), JSONObject.toJSONString(user));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public User getUser(Long userId) {
        try {
            String userKey = RedisConstants.USER_PREFIX.USER_BASE_INFO.key(userId);
            String value = jedisFactory.getInstance().get(userKey);
            if (StringUtils.isNoneBlank(value)) {
                return JSONObject.parseObject(value, User.class);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
