
package com.magic.user.consumer;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.user.service.MemberMongoService;
import com.magic.user.storage.OnlineMemberRedisStorageService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * MemberLoginSucessMongoConsumer
 * 登录成功，更新登陆状态和登陆ip，登陆时间
 *
 * @author zj
 * @date 2017/4/14
 */
@Component("memberLoginSucessMongoConsumer")
@ConsumerConfig(consumerName = "v1memberLoginSucessMongoConsumer", topic =  Topic.MEMBER_LOGIN_SUCCESS)
public class MemberLoginSucessMongoConsumer implements Consumer{

    @Resource
    private MemberMongoService memberMongoService;

    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("member login sucess mq consumer start. key:%s, msg:%s", key, msg));
        try {
            JSONObject object = JSONObject.parseObject(msg);
            long memberId = object.getLongValue("memberId");
            String ip = object.getString("ip");
            long loginTime = object.getLongValue("loginTime");
            return memberMongoService.updateLogin(memberId, ip, loginTime);
        }catch (Exception e){
            ApiLogger.error(String.format("member login sucess mq consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }
}
