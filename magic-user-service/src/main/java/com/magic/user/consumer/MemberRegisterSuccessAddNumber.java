
package com.magic.user.consumer;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.user.entity.Member;
import com.magic.user.service.OwnerStockAgentService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * MemberRegisterSuccessAddNumber
 *
 * 注册成功，更新会员数量
 * @author morton
 * @date 2017/7/2
 */
@Component("memberRegisterSuccessAddNumber")
@ConsumerConfig(consumerName = "v1memberRegisterSuccessAddNumber", topic =  Topic.MEMBER_REGISTER_SUCCESS)
public class MemberRegisterSuccessAddNumber implements Consumer{

    @Resource
    private OwnerStockAgentService ownerStockAgentService;

    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("member register success add member number mq consumer start. key:%s, msg:%s", key, msg));
        try {
            Member member = JSONObject.parseObject(msg, Member.class);

            if (!Optional.ofNullable(member).
                    filter(request -> request.getMemberId() > 0).
                    filter(request -> request.getAgentId() != null && request.getAgentId() > 0).
                    isPresent()){
                return true;
            }
            member.setOwnerId(null);
            member.setStockId(null);
            return ownerStockAgentService.updateMemNumber(member.getAgentId());
        }catch (Exception e){
            ApiLogger.error(String.format("member register success add member consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;

    }
}
