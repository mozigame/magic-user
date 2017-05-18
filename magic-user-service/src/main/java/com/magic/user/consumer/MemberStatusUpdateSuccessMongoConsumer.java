
package com.magic.user.consumer;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.api.commons.tools.IPUtil;
import com.magic.user.entity.Member;
import com.magic.user.enums.AccountStatus;
import com.magic.user.enums.CurrencyType;
import com.magic.user.po.OnLineMember;
import com.magic.user.service.MemberMongoService;
import com.magic.user.vo.MemberConditionVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * MemberStatusUpdateSuccessMongoConsumer
 * 修改会员状态成功，修改mongo中会员状态
 * @author zj
 * @date 2017/4/14
 */
@Component("memberStatusUpdateSuccessMongoConsumer")
@ConsumerConfig(consumerName = "v1memberStatusUpdateSuccessMongoConsumer", topic =  Topic.MEMBER_STATUS_UPDATE_SUCCESS)
public class MemberStatusUpdateSuccessMongoConsumer implements Consumer{

    @Resource
    private MemberMongoService memberMongoService;

    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("member status update success mongo mq consumer start. key:%s, msg:%s", key, msg));
        try {
            JSONObject jsonObject = JSONObject.parseObject(msg);
            Long memberId = jsonObject.getLongValue("memberId");
            Integer status = jsonObject.getIntValue("status");
            return memberMongoService.updateMemberStatus(memberId, status);
        }catch (Exception e){
            ApiLogger.error(String.format("member register sucess mq consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }


}
