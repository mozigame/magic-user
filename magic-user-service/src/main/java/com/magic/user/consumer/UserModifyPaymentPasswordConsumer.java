package com.magic.user.consumer;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.api.commons.utils.StringUtils;
import com.magic.user.service.dubbo.DubboOutAssembleServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

/**
 * User: joey
 * Date: 2017/5/11
 * Time: 17:48
 */
@Component("userModifyPaymentPasswordConsumer")
@ConsumerConfig(consumerName = "v1userModifyPaymentPasswordConsumer", topic = Topic.USER_INFO_MODIFY_SUCCESS)
public class UserModifyPaymentPasswordConsumer implements Consumer {

    @Resource
    private DubboOutAssembleServiceImpl dubboOutAssembleService;

    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("payment pwd modify success mq consumer start. key:%s, msg:%s", key, msg));
        try {
            JSONObject object = JSONObject.parseObject(msg);
            Map<String, Object> map = object.parseObject(object.getString("after"), Map.class);
            if (!Optional.ofNullable(map).filter(payment -> payment.containsKey("paymentPassword")).isPresent()){
                return true;
            }
            String password = (String) map.get("paymentPassword");
            Long ownerId = (Long) map.get("ownerId");
            if (StringUtils.isEmpty(password)){
                return true;
            }
            long uid = Long.parseLong(key);
            return dubboOutAssembleService.updateUserPaymentPassword(uid, password,ownerId);
        } catch (Exception e) {
            ApiLogger.error(String.format("payment pwd modify success mq consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }

}
