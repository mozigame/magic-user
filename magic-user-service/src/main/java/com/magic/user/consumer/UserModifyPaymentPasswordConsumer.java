package com.magic.user.consumer;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.user.entity.AccountOperHistory;
import com.magic.user.entity.User;
import com.magic.user.enums.AccountType;
import com.magic.user.service.AccountOperHistoryService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/11
 * Time: 17:48
 */
@Component("userModifyPaymentPasswordConsumer")
@ConsumerConfig(consumerName = "v1userModifyPaymentPasswordConsumer", topic = Topic.USER_INFO_MODIFY_SUCCESS)
public class UserModifyPaymentPasswordConsumer implements Consumer {

    @Resource
    private AccountOperHistoryService accountOperHistoryService;

    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("payment pwd modify success mq consumer start. key:%s, msg:%s", key, msg));
        try {
            JSONObject object = JSONObject.parseObject(msg);

            return accountOperHistoryService.add(null) > 0;
        } catch (Exception e) {
            ApiLogger.error(String.format("payment pwd modify success mq consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }

}
