package com.magic.user.consumer;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.user.entity.AgentReview;
import com.magic.user.service.AgentReviewService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/13
 * Time: 11:35
 */
@Component("agentReviewSuccessConsumer")
@ConsumerConfig(consumerName = "v1agentReviewSuccessConsumer", topic = Topic.AGENT_REVIEW_HISTORY)
public class AgentReviewSuccessConsumer implements Consumer {

    @Resource
    private AgentReviewService agentReviewService;

    @Override

    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("agent review history mq consumer start. key:%s, msg:%s", key, msg));
        try {
            AgentReview review = JSONObject.parseObject(msg, AgentReview.class);
            if (!agentReviewService.add(review)) {
                ApiLogger.error(String.format("agent review history add failed. agentApplyId:%s", review.getAgentApplyId()));
                return false;
            }
        } catch (Exception e) {
            ApiLogger.error(String.format("user login success mq consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }

}