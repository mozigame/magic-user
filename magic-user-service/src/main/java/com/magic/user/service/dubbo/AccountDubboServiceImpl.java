package com.magic.user.service.dubbo;

import com.magic.user.entity.Member;
import com.magic.user.entity.User;
import com.magic.user.service.MemberService;
import com.magic.user.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * AccountDubboServiceImpl
 *
 * @author zj
 * @date 2017/5/15
 */
@Service("accountDubboService")
public class AccountDubboServiceImpl implements AccountDubboService{

    @Resource
    private UserService userService;
    @Resource
    private MemberService memberService;

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
}
