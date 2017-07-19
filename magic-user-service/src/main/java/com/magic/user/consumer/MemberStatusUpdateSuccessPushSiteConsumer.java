
package com.magic.user.consumer;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.cms.enums.SiteMsgType;
import com.magic.cms.service.MsgDubboService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * MemberStatusUpdateSuccessPushSiteConsumer
 * 修改会员状态成功，修改mongo中会员状态
 * @author zj
 * @date 2017/4/14
 */
@Component("memberStatusUpdateSuccessPushSiteConsumer")
@ConsumerConfig(consumerName = "v1memberStatusUpdateSuccessPushSiteConsumer", topic =  Topic.MEMBER_STATUS_UPDATE_SUCCESS)
public class MemberStatusUpdateSuccessPushSiteConsumer implements Consumer{

    @Resource
    private MsgDubboService msgDubboService;

    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("member status update success site msg mq consumer start. key:%s, msg:%s", key, msg));
        try {
            JSONObject jsonObject = JSONObject.parseObject(msg);
            Long memberId = jsonObject.getLongValue("memberId");
            Integer status = jsonObject.getIntValue("status");
            Long ownerId = jsonObject.getLongValue("ownerId");
            if (status == 2){
                return msgDubboService.saveSiteMsg(SiteMsgType.ACCOUNT_STOP, ownerId, memberId, "玩家帐号被停用");
            }
        }catch (Exception e){
            ApiLogger.error(String.format("member register sucess site msg mq consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }


}
