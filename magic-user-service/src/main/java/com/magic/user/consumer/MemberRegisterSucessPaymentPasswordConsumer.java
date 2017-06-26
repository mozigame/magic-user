
package com.magic.user.consumer;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.api.commons.utils.StringUtils;
import com.magic.user.entity.Member;
import com.magic.user.service.dubbo.DubboOutAssembleServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * MemberRegisterSucessPaymentPasswordConsumer
 *
 * 注册成功，修改支付密码
 * @author zj
 * @date 2017/6/21
 */
@Component("memberRegisterSucessPaymentPasswordConsumer")
@ConsumerConfig(consumerName = "v1memberRegisterSucessPaymentPasswordConsumer", topic =  Topic.MEMBER_REGISTER_SUCCESS)
public class MemberRegisterSucessPaymentPasswordConsumer implements Consumer{

    @Resource
    private DubboOutAssembleServiceImpl dubboOutAssembleService;

    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("member register sucess payment password mq consumer start. key:%s, msg:%s", key, msg));
        try {
            Member member = JSONObject.parseObject(msg, Member.class);
            if (!Optional.ofNullable(member).filter(id -> id.getMemberId() > 0).isPresent()){
                return true;
            }
            String paymentPassword = member.getPaymentPassword();
            if (StringUtils.isEmpty(paymentPassword)){
                return true;
            }
            return dubboOutAssembleService.insertUserPaymentPassword(member.getMemberId(),  member.getOwnerId(), paymentPassword);
        }catch (Exception e){
            ApiLogger.error(String.format("member register sucess payment password consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }
}
