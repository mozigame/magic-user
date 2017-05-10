
package com.magic.user.consumer;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.user.storage.OnlineMemberRedisStorageService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * MemberLoginSucessConsumer
 * 登录成功，记录业主在线会员数
 *
 * @author zj
 * @date 2017/4/14
 */
@Component("memberLoginSucessConsumer")
@ConsumerConfig(consumerName = "v1memberLoginSucessConsumer", topic =  Topic.MEMBER_LOGIN_SUCCESS)
public class MemberLoginSucessConsumer implements Consumer{

    @Resource
    private OnlineMemberRedisStorageService onlineMemberRedisStorageService;

    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("member login sucess mq consumer start. key:%s, msg:%s", key, msg));
        try {
            JSONObject object = JSONObject.parseObject(msg);
            long memberId = object.getLongValue("memberId");
            long ownerId = object.getLongValue("ownerId");
            return onlineMemberRedisStorageService.addOnlineMember(ownerId, memberId);
        }catch (Exception e){
            ApiLogger.error(String.format("member sucess mq consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }
}
