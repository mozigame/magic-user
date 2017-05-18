package com.magic.user.consumer;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.user.entity.AccountOperHistory;
import com.magic.user.entity.Member;
import com.magic.user.entity.User;
import com.magic.user.enums.AccountType;
import com.magic.user.po.OnLineMember;
import com.magic.user.service.AccountOperHistoryService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/11
 * Time: 17:48
 */
@Component("userModifyHistoryConsumer")
@ConsumerConfig(consumerName = "v1userModifyHistoryConsumer", topic = Topic.USER_INFO_MODIFY_SUCCESS)
public class UserModifyHistoryConsumer implements Consumer {

    @Resource
    private AccountOperHistoryService accountOperHistoryService;


    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("user modify success mq consumer start. key:%s, msg:%s", key, msg));
        try {
            JSONObject object = JSONObject.parseObject(msg);
            AccountOperHistory history=assembleOperHistory(object);
            return accountOperHistoryService.add(history) > 0;
        } catch (Exception e) {
            ApiLogger.error(String.format("user modify success mq consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }

    private AccountOperHistory assembleOperHistory(JSONObject object) {
        User operator = JSONObject.parseObject(object.getString("operator"), User.class);
        Map userMap = JSONObject.parseObject(object.getString("user"), Map.class);
        AccountOperHistory operHistory = new AccountOperHistory();
        operHistory.setType(AccountType.parse((Integer) userMap.get("type")));
        operHistory.setUsername((String) userMap.get("username"));
        operHistory.setOwnerId(Long.parseLong(String.valueOf(userMap.get("ownerId"))));
        operHistory.setOwnerName((String) userMap.get("ownerName"));
        operHistory.setUserId(Long.parseLong(String.valueOf(userMap.get("userId"))));
        operHistory.setCreateTime(Long.parseLong(String.valueOf(userMap.get("operTime"))));
        operHistory.setBeforeInfo(object.getString("before"));
        operHistory.setAfterInfo(object.getString("after"));
        operHistory.setProcUserId(operator.getUserId());
        operHistory.setProcUsername(operator.getUsername());
        return operHistory;
    }
}
