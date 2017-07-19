
package com.magic.user.consumer;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.cms.enums.SiteMsgType;
import com.magic.user.entity.Member;
import com.magic.user.service.dubbo.DubboOutAssembleServiceImpl;
import com.magic.user.service.thrift.ThriftOutAssembleServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * MemberRegisterSuccessPushMsg
 *
 * 注册成功，推送注册数据
 * @author morton
 * @date 2017/7/2
 */
@Component("memberRegisterSuccessPushSiteMsg")
@ConsumerConfig(consumerName = "v1memberRegisterSuccessPushSiteMsg", topic =  Topic.MEMBER_REGISTER_SUCCESS)
public class MemberRegisterSuccessPushSiteMsg implements Consumer{

    @Resource
    private DubboOutAssembleServiceImpl dubboOutAssembleService;

    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("member register success push site msg mq consumer start. key:%s, msg:%s", key, msg));
        try {
            Member member = JSONObject.parseObject(msg, Member.class);
            if (!Optional.ofNullable(member).
                    filter(request -> request.getMemberId() > 0).
                    filter(request -> request.getAgentId() != null && request.getAgentId() > 0).
                    isPresent()){
                return true;
            }
            return dubboOutAssembleService.sendSiteMsg(SiteMsgType.REGISTER, member.getMemberId(), member.getOwnerId(), "新用户注册成功");
        }catch (Exception e){
            ApiLogger.error(String.format("member register success push site msg consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }
}
