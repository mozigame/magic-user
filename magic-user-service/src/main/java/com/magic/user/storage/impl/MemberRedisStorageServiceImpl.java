package com.magic.user.storage.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.codis.JedisFactory;
import com.magic.api.commons.tools.IPUtil;
import com.magic.api.commons.utils.StringUtils;
import com.magic.user.constants.RedisConstants;
import com.magic.user.constants.UserContants;
import com.magic.user.entity.Member;
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

    @Override
    public Member getMember(Long memberId) {
        try {
            String key = RedisConstants.assembleMemberInfo(memberId);
            String result = jedisFactory.getInstance().get(key);
            if (StringUtils.isNotBlank(result)) {
                return JSONObject.parseObject(result, Member.class);
            }
        } catch (Exception e) {
            ApiLogger.error("get member info error , memberId : "+memberId, e);
        }
        return null;
    }

    @Override
    public boolean setMember(Member member) {
        try {
            String key = RedisConstants.assembleMemberInfo(member.getMemberId());
            Jedis jedis = jedisFactory.getInstance();
            jedis.set(key, JSONObject.toJSONString(member));
            jedis.expire(key, RedisConstants.THREE_DAY_EXPIRE);
            return true;
        } catch (Exception e) {
            ApiLogger.error("set member info error , member : "+ JSON.toJSONString(member), e);
        }
        return false;
    }

    @Override
    public boolean delsetMember(Long memberId) {
        try {
            String key = RedisConstants.assembleMemberInfo(memberId);
            Long result = jedisFactory.getInstance().del(key);
            return result != null && result > 0L;
        } catch (Exception e) {
            ApiLogger.error("del redis member info error , memberId : "+ memberId, e);
        }
        return false;
    }
}
