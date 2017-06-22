
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
import com.magic.user.service.thrift.ThriftOutAssembleServiceImpl;
import com.magic.user.vo.MemberConditionVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * MemberRegisterSucessThriftConsumer
 * 注册成功，创建游戏账户
 * @author zj
 * @date 2017/6/21
 */
@Component("memberRegisterSucessThriftConsumer")
@ConsumerConfig(consumerName = "v1memberRegisterSucessThriftConsumer", topic =  Topic.MEMBER_REGISTER_SUCCESS)
public class MemberRegisterSucessThriftConsumer implements Consumer{

    @Resource
    private ThriftOutAssembleServiceImpl thriftOutAssembleService;

    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("member register sucess payment acccount mq consumer start. key:%s, msg:%s", key, msg));
        try {
            Member member = JSONObject.parseObject(msg, Member.class);
            if (!Optional.ofNullable(member).filter(id -> id.getMemberId() > 0).isPresent()){
                return true;
            }
            String body = assembleBody(member);
            return thriftOutAssembleService.registerPaymentAcccount(body);
        }catch (Exception e){
            ApiLogger.error(String.format("member register sucess payment acccount consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }

    /**
     * 组装body数据
     *
     * @param member
     * @return
     */
    private String assembleBody(Member member) {
        JSONObject body = new JSONObject();
        body.put("UserId", member.getMemberId());
        body.put("UserName", member.getUsername());
        body.put("AgentId", member.getAgentId());
        body.put("AgentName", member.getAgentUsername());
        body.put("OwnerId", member.getOwnerId());
        body.put("OwnerName", member.getOwnerUsername());
        body.put("BankCode", member.getBank() == null ? "" : member.getBank());
        body.put("BankCardNum", member.getBankCardNo() == null ? "" : member.getBankCardNo());
        body.put("BankCardHolder", member.getBankDeposit() == null ? "" : member.getBankDeposit());
        return body.toJSONString();
    }


}
