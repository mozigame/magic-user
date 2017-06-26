
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
 * MemberRegisterSucessMongoConsumer
 * 注册成功，保存会员记录
 * @author zj
 * @date 2017/4/14
 */
@Component("memberRegisterSucessMongoConsumer")
@ConsumerConfig(consumerName = "v1memberRegisterSucessMongoConsumer", topic =  Topic.MEMBER_REGISTER_SUCCESS)
public class MemberRegisterSucessMongoConsumer implements Consumer{

    @Resource
    private MemberMongoService memberMongoService;

    @Override
    public boolean doit(String topic, String tags, String key, String msg) {
        ApiLogger.info(String.format("member register sucess mongo mq consumer start. key:%s, msg:%s", key, msg));
        try {
            Member member = JSONObject.parseObject(msg, Member.class);
            OnLineMember lineMember = parseOnlineMember(member);
            MemberConditionVo vo = parseMemberConditionVo(member);
            if (!memberMongoService.saveMemberInfo(vo)) {
                return false;
            }
            return memberMongoService.saveOnlieMember(lineMember);
        }catch (Exception e){
            ApiLogger.error(String.format("member register sucess mq consumer error. key:%s, msg:%s", key, msg), e);
        }
        return true;
    }

    /**
     * 组装在线会员对象
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
     * @Doc 组装会员存储mongo的数据
     * @param member
     * @return
     */
    private MemberConditionVo parseMemberConditionVo(Member member) {
        MemberConditionVo vo=new MemberConditionVo();
        vo.setMemberId(member.getMemberId());
        vo.setMemberName(member.getUsername());
        vo.setAgentId(member.getAgentId());
        vo.setAgentName(member.getAgentUsername());
        vo.setRegisterTime(member.getRegisterTime());
        vo.setUpdateTime(member.getRegisterTime());
        vo.setStatus(AccountStatus.enable.value());
        vo.setLevel(0);
        vo.setDepositCount(0);
        vo.setWithdrawCount(0);
        vo.setWithdrawMoney(0L);
        vo.setDepositMoney(0L);
        vo.setLastDepositMoney(0L);
        vo.setLastWithdrawMoney(0L);
        vo.setMaxDepositMoney(0);
        vo.setMaxWithdrawMoney(0);
        vo.setCurrencyType(CurrencyType.CNY.value());
        vo.setOwnerId(member.getOwnerId());
        vo.setStockId(member.getStockId());
        return vo;
    }
}
