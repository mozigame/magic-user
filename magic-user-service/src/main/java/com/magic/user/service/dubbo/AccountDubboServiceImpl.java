package com.magic.user.service.dubbo;

import com.magic.api.commons.model.PageBean;
import com.magic.user.entity.Login;
import com.magic.user.entity.Member;
import com.magic.user.entity.User;
import com.magic.user.po.OwnerStaticInfo;
import com.magic.user.service.AccountIdMappingService;
import com.magic.user.service.LoginService;
import com.magic.user.service.MemberService;
import com.magic.user.service.UserService;
import com.magic.user.storage.CountRedisStorageService;
import com.magic.user.util.PasswordCapture;
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
//        if (!Optional.ofNullable(ownerIds).filter(size -> size.size() > 0).isPresent()){
//            return map;
//        }

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


}
