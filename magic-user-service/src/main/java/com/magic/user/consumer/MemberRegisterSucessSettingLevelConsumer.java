
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
 * MemberRegisterSucessThriftConsumer
 * 注册成功，设置会员层级
 * @author zj
 * @date 2017/6/21
 */
@Component("memberRegisterSucessSettingLevelConsumer")
@ConsumerConfig(consumerName = "v1memberRegisterSucessSettingLevelConsumer", topic =  Topic.MEMBER_REGISTER_SUCCESS)
public class MemberRegisterSucessSettingLevelConsumer implements Consumer{

    @Resource
    private ThriftOutAssembleServiceImpl thriftOutAssembleService;

    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("member register sucess settings level mq consumer start. key:%s, msg:%s", key, msg));
        try {
            Member member = JSONObject.parseObject(msg, Member.class);
            if (!Optional.ofNullable(member).filter(id -> id.getMemberId() > 0).isPresent()){
                return true;
            }
            String body = assembleBody(member);
            return thriftOutAssembleService.settingLevel(body);
        }catch (Exception e){
            ApiLogger.error(String.format("member register sucess settings level consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }

    /**
     * 组装body
     *
     * @param member
     * @return
     */
    private String assembleBody(Member member) {
        JSONObject object = new JSONObject();
        object.put("UserId", member.getMemberId());
        object.put("UserName", member.getUsername());
        object.put("OwnerId", member.getOwnerId());
        object.put("AgentId", member.getAgentId());
        object.put("RegisterTime", member.getRegisterTime());
        return object.toJSONString();
    }


}
