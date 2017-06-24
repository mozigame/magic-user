package com.magic.user.consumer;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.api.commons.tools.IPUtil;
import com.magic.config.enums.LogCategory;
import com.magic.config.enums.LogType;
import com.magic.config.service.dubbo.LogDubboService;
import com.magic.user.entity.Login;
import com.magic.user.entity.LoginHistory;
import com.magic.user.entity.User;
import com.magic.user.enums.AccountType;
import com.magic.user.enums.LoginType;
import com.magic.user.service.LoginHistoryService;
import com.magic.user.service.LoginService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * User: jack
 * Date: 2017/6/24
 * Time: 10:29
 */
@Component("userLoginLogSuccessConsumer")
@ConsumerConfig(consumerName = "v1userLoginLogSuccessConsumer", topic = Topic.USER_LOGIN_SUCCESS)
public class UserLoginLogSuccessConsumer implements Consumer {

    @Resource
    private LogDubboService logDubboService;

    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("user login success write to log mq consumer start. key:%s, msg:%s", key, msg));
        try {
            JSONObject object = JSONObject.parseObject(msg);
            User user = JSONObject.parseObject(object.getString("user"), User.class);
            AccountType type = user.getType();
            Integer requestIp = object.getIntValue("requestIp");
            LogCategory category = LogCategory.SITE;
            if (type == AccountType.agent){
                category = LogCategory.AGENT;
            }
            return logDubboService.sendLog(category, LogType.LOGIN, "登陆成功", user.getUserId(), user.getUsername(), IPUtil.intToIp(requestIp));
        } catch (Exception e) {
            ApiLogger.error(String.format("user login success write to log mq consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }
}
