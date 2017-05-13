package com.magic.user.consumer;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.user.entity.LoginHistory;
import com.magic.user.enums.LoginType;
import com.magic.user.service.LoginHistoryService;
import com.magic.user.service.LoginService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/13
 * Time: 11:28
 */
@Component("agentAddSuccessConsumer")
@ConsumerConfig(consumerName = "v1agentAddSuccessConsumer", topic = Topic.AGENT_ADD_SUCCESS)
public class AgentAddSuccessConsumer implements Consumer {



    @Override

    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("user login success mq consumer start. key:%s, msg:%s", key, msg));
        try {
            JSONObject object = JSONObject.parseObject(msg);

        } catch (Exception e) {
            ApiLogger.error(String.format("user login success mq consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }

}

