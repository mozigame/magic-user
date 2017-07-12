
package com.magic.user.consumer;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.user.entity.Member;
import com.magic.user.service.thrift.ThriftOutAssembleServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * MemberLoginSucessPushMsgConsumer
 * 登录成功，推送消息
 *
 * @author zj
 * @date 2017/4/14
 */
@Component("memberLoginSucessPushMsgConsumer")
@ConsumerConfig(consumerName = "v1memberLoginSucessPushMsgConsumer", topic =  Topic.MEMBER_LOGIN_SUCCESS)
public class MemberLoginSucessPushMsgConsumer implements Consumer{

    @Resource
    private ThriftOutAssembleServiceImpl thriftOutAssembleService;

    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("member login success push msg consumer start. key:%s, msg:%s", key, msg));
        try {
            JSONObject object = JSONObject.parseObject(msg);
            Member member = JSONObject.parseObject(object.getString("member"), Member.class);
            if (!Optional.ofNullable(member).isPresent()){
                return true;
            }
            String body = assembleBody(object);
            return thriftOutAssembleService.pushMsg(body);
        }catch (Exception e){
            ApiLogger.error(String.format("member login success push msg consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }

    /**
     * 组装body
     *
     * @param object
     * @return
     */
    private String assembleBody(JSONObject object) {
        JSONObject body = new JSONObject();
        body.put("ReqType", 2);
        body.put("Msg", object);
        return body.toJSONString();
    }
}
