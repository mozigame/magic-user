
package com.magic.user.consumer;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.user.service.AgentMongoService;
import com.magic.user.service.MemberMongoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * MemberStatusUpdateSuccessMongoConsumer
 * 修改会员状态成功，修改mongo中会员状态
 * @author zj
 * @date 2017/4/14
 */
@Component("agentStatusUpdateSuccessMongoConsumer")
@ConsumerConfig(consumerName = "v1agentStatusUpdateSuccessMongoConsumer", topic =  Topic.AGENT_STATUS_UPDATE_SUCCESS)
public class AgentStatusUpdateSuccessMongoConsumer implements Consumer{

    @Resource(name = "agentMongoService")
    private AgentMongoService agentMongoService;

    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("agent status update success mongo mq consumer start. key:%s, msg:%s", key, msg));
        try {
            JSONObject jsonObject = JSONObject.parseObject(msg);
            Long agentId = jsonObject.getLongValue("agentId");
            Integer status = jsonObject.getIntValue("status");
            return agentMongoService.updateStatus(agentId, status);
        }catch (Exception e){
            ApiLogger.error(String.format("agent status update success mongo mq consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }


}
