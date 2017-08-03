
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
import com.magic.user.service.AgentMongoService;
import com.magic.user.service.MemberMongoService;
import com.magic.user.service.thrift.ThriftOutAssembleServiceImpl;
import com.magic.user.vo.AgentConditionVo;
import com.magic.user.vo.MemberConditionVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * MemberRegisterSucessMongoConsumer
 * 注册成功，保存会员记录
 *
 * @author zj
 * @date 2017/4/14
 */
@Component("memberRegisterSucessMongoConsumer")
@ConsumerConfig(consumerName = "v1memberRegisterSucessMongoConsumer", topic = Topic.MEMBER_REGISTER_SUCCESS)
public class MemberRegisterSucessMongoConsumer implements Consumer {

    @Resource
    private MemberMongoService memberMongoService;
    @Resource
    private ThriftOutAssembleServiceImpl thriftOutAssembleService;
    @Resource
    private AgentMongoService agentMongoService;

    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("member register sucess mongo mq consumer start. key:%s, msg:%s", key, msg));
        try {
            Member member = JSONObject.parseObject(msg, Member.class);
            boolean result;
            OnLineMember onlineMember = memberMongoService.getOnlineMember(member.getMemberId());
            if (!Optional.ofNullable(onlineMember).isPresent()) {
                OnLineMember lineMember = parseOnlineMember(member);
                result = memberMongoService.saveOnlieMember(lineMember);
                if (!result) {
                    return false;
                }
            }
            MemberConditionVo conditionVo = memberMongoService.get(member.getMemberId());
            if (!Optional.ofNullable(conditionVo).isPresent()) {
                MemberConditionVo vo = parseMemberConditionVo(member);
                long level = thriftOutAssembleService.settingLevel(member);
                if (level <= 0) {
                    level = thriftOutAssembleService.getMemberLevel(member.getMemberId());
                }
                if (level <= 0) {
                    return false;
                }
                vo.setLevel(level);
                result = memberMongoService.saveMemberInfo(vo);
                if (!result) {
                    return false;
                }
            }
            //累加代理的会员数（members） -并发会有问题
            AgentConditionVo agentConditionVo = agentMongoService.get(member.getAgentId());
            if (agentConditionVo != null) {
                agentConditionVo.setMembers(agentConditionVo.getMembers() + 1);
                result = agentMongoService.updateAgent(agentConditionVo);
                if (!result) {
                    ApiLogger.error(String.format("mongoUpdate member result = " + result)
                            + ", key =  + " + key + ", member = " + (member!=null?member.getAgentId():null));
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            ApiLogger.error(String.format("member register sucess mq consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }

    /**
     * 组装在线会员对象
     *
     * @param member
     * @return
     */
    private OnLineMember parseOnlineMember(Member member) {
        OnLineMember lineMember = new OnLineMember();
        lineMember.setMemberId(member.getMemberId());
        lineMember.setAccount(member.getUsername());
        lineMember.setOwnerId(member.getOwnerId());
        lineMember.setHolderId(member.getStockId());
        lineMember.setAgentId(member.getAgentId());
        lineMember.setRegisterTime(member.getRegisterTime());
        lineMember.setRegisterIp(IPUtil.intToIp(member.getRegisterIp()));
        lineMember.setStatus(1);//注销
        return lineMember;
    }

    /**
     * @param member
     * @return
     * @Doc 组装会员存储mongo的数据
     */
    private MemberConditionVo parseMemberConditionVo(Member member) {
        MemberConditionVo vo = new MemberConditionVo();
        vo.setMemberId(member.getMemberId());
        vo.setMemberName(member.getUsername());
        vo.setAgentId(member.getAgentId());
        vo.setAgentName(member.getAgentUsername());
        vo.setRegisterTime(member.getRegisterTime());
        vo.setUpdateTime(member.getRegisterTime());
        vo.setStatus(AccountStatus.enable.value());
        vo.setDepositCount(0);
        vo.setWithdrawCount(0);
        vo.setWithdrawMoney(0L);
        vo.setDepositMoney(0L);
        vo.setLastDepositMoney(0L);
        vo.setLastWithdrawMoney(0L);
        vo.setMaxDepositMoney(0L);
        vo.setMaxWithdrawMoney(0L);
        vo.setCurrencyType(CurrencyType.CNY.value());
        vo.setOwnerId(member.getOwnerId());
        vo.setStockId(member.getStockId());
        vo.setTelephone(member.getTelephone());
        vo.setBankCardNo(member.getBankCardNo());
        return vo;
    }
}
