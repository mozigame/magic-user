package com.magic.user.consumer;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.user.entity.Login;
import com.magic.user.entity.LoginHistory;
import com.magic.user.entity.User;
import com.magic.user.enums.LoginType;
import com.magic.user.service.LoginHistoryService;
import com.magic.user.service.LoginService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/13
 * Time: 10:15
 * @Doc 用户注销成功的历史记录
 */
@Component("userLogoutSuccessConsumer")
@ConsumerConfig(consumerName = "v1userLogoutSuccessConsumer", topic = Topic.USER_LOGOUT_SUCCESS)
public class UserLogoutSuccessConsumer implements Consumer {

    @Resource
    private LoginService loginService;

    @Resource
    private LoginHistoryService loginHistoryService;

    @Override

    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("user logout success mq consumer start. key:%s, msg:%s", key, msg));
        try {
            JSONObject object = JSONObject.parseObject(msg);
            User user = JSONObject.parseObject(object.getString("user"), User.class);
            Long userId = user.getUserId();
            Long createTime = object.getLongValue("createTime");
            Integer requestIp = object.getIntValue("requestIp");
            String platform = object.getString("platform");
            if (!loginService.updateLoginStatus(userId, null, null, LoginType.logout.value())) {
                ApiLogger.error("user logout status update error: userId:" + userId);
                Login login = loginService.get(userId);
                if (login != null && login.getStatus() != LoginType.logout) {
                    return false;
                }
            }
            LoginHistory history = assembleLoginHistory(userId, createTime, requestIp, LoginType.logout, platform);
            if (!loginHistoryService.add(history)) {
                return false;
            }
        } catch (Exception e) {
            ApiLogger.error(String.format("user logout success mq consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }

    //TODO 注释
    private LoginHistory assembleLoginHistory(Long userId, Long createTime, Integer requestIp, LoginType loginType, String platform) {
        LoginHistory history = new LoginHistory();
        history.setUserId(userId);
        history.setCreateTime(createTime);
        history.setRequestIp(requestIp);
        history.setLoginType(loginType);
        history.setPlatform(platform);
        return history;
    }
}
