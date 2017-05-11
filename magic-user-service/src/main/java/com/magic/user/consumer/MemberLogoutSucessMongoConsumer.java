
package com.magic.user.consumer;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.user.service.MemberMongoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * MemberLogoutSucessMongoConsumer
 * 注销成功，更新登陆状态
 *
 * @author zj
 * @date 2017/4/14
 */
@Component("memberLogoutSucessMongoConsumer")
@ConsumerConfig(consumerName = "v1memberLogoutSucessMongoConsumer", topic =  Topic.MEMBER_LOGOUT_SUCCESS)
public class MemberLogoutSucessMongoConsumer implements Consumer{

    @Resource
    private MemberMongoService memberMongoService;

    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("member logout sucess mq consumer start. key:%s, msg:%s", key, msg));
        try {
            long memberId = Long.parseLong(key);
            return memberMongoService.updateLogout(memberId);
        }catch (Exception e){
            ApiLogger.error(String.format("member logout sucess mq consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }
}
