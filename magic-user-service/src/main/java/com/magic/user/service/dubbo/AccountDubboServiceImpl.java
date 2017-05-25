package com.magic.user.service.dubbo;

import com.magic.api.commons.model.PageBean;
import com.magic.user.entity.Login;
import com.magic.user.entity.Member;
import com.magic.user.entity.User;
import com.magic.user.service.AccountIdMappingService;
import com.magic.user.service.LoginService;
import com.magic.user.service.MemberService;
import com.magic.user.service.UserService;
import com.magic.user.util.PasswordCapture;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
