package com.magic.user.service.dubbo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.model.PageBean;
import com.magic.api.commons.tools.IPUtil;
import com.magic.config.thrift.base.EGResp;
import com.magic.user.constants.UserContants;
import com.magic.user.entity.Login;
import com.magic.user.entity.Member;
import com.magic.user.entity.User;
import com.magic.user.enums.AccountStatus;
import com.magic.user.exception.UserException;
import com.magic.user.po.OwnerStaticInfo;
import com.magic.user.service.*;
import com.magic.user.service.thrift.ThriftOutAssembleServiceImpl;
import com.magic.user.storage.CountRedisStorageService;
import com.magic.user.util.PasswordCapture;
import com.magic.user.util.UserUtil;
import com.magic.user.vo.MemberConditionVo;
import com.magic.user.vo.MemberInfoVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * AccountDubboServiceImpl
 *
 * @author zj
 * @date 2017/5/15
 */
@Service("accountDubboService")
public class AccountDubboServiceImpl implements AccountDubboService {

    @Resource
    private UserService userService;
    @Resource
    private MemberService memberService;
    @Resource(name = "loginService")
    private LoginService loginService;
    @Resource
    private AccountIdMappingService accountIdMappingService;
    @Resource
    private CountRedisStorageService countRedisStorageService;
    @Resource
    private ThriftOutAssembleServiceImpl thriftOutAssembleService;
    @Resource
    private MemberMongoService memberMongoService;

    /**
     * {@inheritDoc}
     */
    @Override
    public long getOwnerId(long uid) {
        User user = userService.get(uid);
        return user != null ? user.getOwnerId() : 0l;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUser(long uid) {
        return userService.get(uid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Member getMember(long uid) {
        return memberService.getMemberById(uid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Long, OwnerStaticInfo> getOwnerStaticInfo(Collection<Long> ownerIds) {
        Map<Long, OwnerStaticInfo> map = new HashMap<>();
        if (!Optional.ofNullable(ownerIds).filter(size -> size.size() > 0).isPresent()){
            return map;
        }

        Iterator<Long> iterator = ownerIds.iterator();
        while (iterator.hasNext()){
            Long ownerId = iterator.next();
            OwnerStaticInfo info = new OwnerStaticInfo();
            info.setOwnerId(ownerId);
            info.setStocks(countRedisStorageService.getStocks(ownerId));
            info.setAgents(countRedisStorageService.getAgents(ownerId));
            info.setMembers(countRedisStorageService.getMembers(ownerId));
            info.setWorkers(countRedisStorageService.getWorkers(ownerId));
            map.put(ownerId, info);
        }
        return map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Member checkMemberLogin(long uid) {
        Member member = memberService.getMemberById(uid);
        if (!Optional.ofNullable(member).filter(status -> status.getStatus() == AccountStatus.enable).isPresent()){
            ApiLogger.error(String.format("member was disable. uid: %d, member: %s", uid, JSON.toJSONString(member)));
            return null;
        }
        boolean result = checkLogin(member);
        if (!result){
            ApiLogger.error(String.format("member not logined uid: %d", uid));
            return null;
        }
        return member;
    }

    /**
     * 检查登录状态
     * @param member
     * @return
     */
    private boolean checkLogin(Member member) {
        String body = assembleVerifyBody(member);
        EGResp resp = thriftOutAssembleService.memberLoginVerify(body, UserContants.CALLER);
        return Optional.ofNullable(resp).filter(response -> response.getCode() == 0x3333).isPresent();
    }

    /**
     * 组装登陆校验passport 请求body
     *
     * @param member 用户
     * @return
     */
    private String assembleVerifyBody(Member member) {
        JSONObject object = new JSONObject();
        object.put("userId", member.getMemberId());
        object.put("username", member.getUsername());
        return object.toJSONString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MemberInfoVo getMemberInfo(long memberId) {
        MemberConditionVo mcv = memberMongoService.get(memberId);
        MemberInfoVo result = assembleMemberInfoVo(mcv);
        return result;
    }

    /**
     * {@inheritDoc}
     * @param startTime
     * @param endTime
     * @param ownerId
     * @return
     */
    @Override
    public List<User> periodAgentList(Long startTime, Long endTime, Long ownerId) {
        return userService.periodAgentList(startTime, endTime, ownerId);
    }


    /**
     * 组装登会员的股东ID，业主ID及层级信息
     * @param mcv
     * @return
     */
    private MemberInfoVo assembleMemberInfoVo(MemberConditionVo mcv) {
        if(mcv == null){
            return null;
        }
        MemberInfoVo result = new MemberInfoVo();
        result.setLevel(mcv.getLevel());
        result.setStockId(mcv.getStockId());
        result.setOwnerId(mcv.getOwnerId());
        return result;
    }

}
