package com.magic.user.consumer;

/**
 * MemberLoginRedisCountConsumer
 *
 * @author zj
 * @date 2017/5/31
 */

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.api.commons.tools.LocalDateTimeUtil;
import com.magic.user.constants.RedisConstants;
import com.magic.user.entity.Member;
import com.magic.user.storage.CountRedisStorageService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * MemberLoginRedisCountConsumer
 * 登录成功，增加计数器
 *
 * @author zj
 * @date 2017/5/31
 */
@Component("memberLoginRedisCountConsumer")
@ConsumerConfig(consumerName = "v1memberLoginRedisCountConsumer", topic =  Topic.MEMBER_LOGIN_SUCCESS)
public class MemberLoginRedisCountConsumer implements Consumer {

    @Resource
    private CountRedisStorageService countRedisStorageService;

    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("member login sucess of redis count mq consumer start. key:%s, msg:%s", key, msg));
        try {
            JSONObject object = JSONObject.parseObject(msg);
            Member member = JSONObject.parseObject(object.getString("member"), Member.class);
            if (!Optional.ofNullable(member).isPresent()){
                return true;
            }
            long memberId = member.getMemberId();
            long loginTime = object.getLongValue("loginTime");
            String date = LocalDateTimeUtil.toAmerica(loginTime, LocalDateTimeUtil.YYYYMMDD);
            String countKey = RedisConstants.assmbleIncrKey(memberId, date);
            long incr = countRedisStorageService.incr(countKey);
            if (incr == 0){
                ApiLogger.info(String.format("incr error, try again. key: %s, msg: %s", key, msg));
                return false;
            }
            if (incr == 1) {//当天第一次
                boolean result = countRedisStorageService.countLogins(member, date);
                if (!result){
                    countRedisStorageService.decr(countKey);
                }
                return result;
            }
        } catch (Exception e) {
            ApiLogger.error(String.format("member login sucess of redis count mq consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }



}
