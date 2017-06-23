package com.magic.user.consumer;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.mq.annotation.ConsumerConfig;
import com.magic.api.commons.mq.api.Consumer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.api.commons.utils.StringUtils;
import com.magic.config.thrift.base.EGResp;
import com.magic.user.constants.UserContants;
import com.magic.user.entity.AgentConfig;
import com.magic.user.entity.OwnerStockAgentMember;
import com.magic.user.service.AgentConfigService;
import com.magic.user.service.AgentMongoService;
import com.magic.user.service.OwnerStockAgentService;
import com.magic.user.service.thrift.ThriftOutAssembleServiceImpl;
import com.magic.user.vo.AgentConditionVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/13
 * Time: 11:28
 */
@Component("agentAddSuccessConsumer")
@ConsumerConfig(consumerName = "v1agentAddSuccessConsumer", topic = Topic.AGENT_ADD_SUCCESS)
public class AgentAddSuccessConsumer implements Consumer {

    @Resource
    private AgentConfigService agentConfigService;
    @Resource
    private OwnerStockAgentService ownerStockAgentService;
    @Resource
    private AgentMongoService agentMongoService;
    @Resource
    private ThriftOutAssembleServiceImpl thriftOutAssembleService;

    @Override

    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("agent add success mq consumer start. key:%s, msg:%s", key, msg));
        try {
            JSONObject object = JSONObject.parseObject(msg);
            String agentConfigStr = object.getString(UserContants.CUSTOMER_MQ_TAG.AGENT_CONFIG_ADD.name());
            String userIdMappingStr = object.getString(UserContants.CUSTOMER_MQ_TAG.USER_ID_MAPPING_ADD.name());
            String agentConditionStr = object.getString(UserContants.CUSTOMER_MQ_TAG.AGENT_CONDITION_ADD.name());
            if (StringUtils.isBlank(agentConfigStr) || StringUtils.isBlank(userIdMappingStr) || StringUtils.isBlank(agentConditionStr)) {
                ApiLogger.error("agent add success mq consumer no agentConfigStr or userIdMappingStr");
                return false;
            }
            //TODO 代理参数添加已经改为调用thrift接口，这里暂时用，优化时删除
            AgentConfig agentConfig = JSONObject.parseObject(agentConfigStr, AgentConfig.class);
            if (!agentConfigService.add(agentConfig)) {
                ApiLogger.error(String.format("agent add success mq consumer add agentConfig failed.agentId:%d", agentConfig.getAgentId()));
                if (agentConfigService.get(agentConfig.getAgentId()) == null) {
                    return false;
                }
            }

            EGResp resp = thriftOutAssembleService.addAgentConfig(assembleConfigBody(agentConfig), "account");
            if (resp == null || resp.getCode() != 0) {
                ApiLogger.error("add agengConfig by thrift is failed, code :" + resp.getCode());
            }

            OwnerStockAgentMember ownerAccountUser = JSONObject.parseObject(userIdMappingStr, OwnerStockAgentMember.class);
            if (!ownerStockAgentService.add(ownerAccountUser)) {
                ApiLogger.error(String.format("agent add success mq consumer add ownerAccountUser failed.ownerId:%d, stockId:%d, agentId:%d", ownerAccountUser.getOwnerId(), ownerAccountUser.getStockId(), agentConfig.getAgentId()));
                if (ownerStockAgentService.findById(ownerAccountUser) == null) {
                    return false;
                }
            }

            AgentConditionVo agentConditionVo = JSONObject.parseObject(agentConditionStr, AgentConditionVo.class);
            if (!agentMongoService.saveAgent(agentConditionVo)) {
                ApiLogger.error(String.format("agent add success mq consumer add agentConditionVo failed.agentId:%d", agentConditionVo.getAgentId()));
                if (agentMongoService.get(agentConditionVo.getAgentId()) == null) {
                    return false;
                }
            }


        } catch (Exception e) {
            ApiLogger.error(String.format("agent add success mq consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }

    private String assembleConfigBody(AgentConfig config) {
        JSONObject object = new JSONObject();
        object.put("ownerId", config.getOwnerId());
        object.put("agentId", config.getAgentId());
        object.put("returnScheme", config.getReturnSchemeId());
        object.put("adminCost", config.getAdminCostId());
        object.put("feeScheme", config.getFeeId());
        object.put("discount", config.getDiscount());
        object.put("cost", config.getCost());
        return object.toJSONString();
    }


}

