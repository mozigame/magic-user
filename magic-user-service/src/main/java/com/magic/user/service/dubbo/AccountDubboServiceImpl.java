package com.magic.user.service.dubbo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.model.PageBean;
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
import com.magic.user.vo.MemberConditionVo;
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
        boolean result = checkLogin(uid);
        if (!result){
            ApiLogger.error(String.format("member not logined uid: %d", uid));
            return null;
        }
        Member member = memberService.getMemberById(uid);
        if (!Optional.ofNullable(member).filter(status -> status.getStatus() == AccountStatus.enable).isPresent()){
            ApiLogger.error(String.format("member was disable. uid: %d, member: %s", uid, JSON.toJSONString(member)));
            return null;
        }
        return member;
    }

    /**
     * 检查登录状态
     * @param uid
     * @return
     */
    private boolean checkLogin(long uid) {
        String body = assembleVerifyBody(uid);
        EGResp resp = thriftOutAssembleService.memberLoginVerify(body, UserContants.CALLER);
        return Optional.ofNullable(resp).filter(response -> response.getCode() == 0x3333).isPresent();
    }

    /**
     * 组装登陆校验passport 请求body
     *
     * @param uid 用户ID
     * @return
     */
    private String assembleVerifyBody(long uid) {
        JSONObject object = new JSONObject();
        object.put("userId", uid);
        object.put("operatorTime", System.currentTimeMillis());
        return object.toJSONString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MemberConditionVo getMemberConditionByMemberId(long memberId) {
        return memberMongoService.get(memberId);
    }

}
