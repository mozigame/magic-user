package com.magic.user.member.resource.service.impl;

import com.magic.api.commons.model.Page;
import com.magic.user.entity.Member;
import com.magic.user.entity.User;
import com.magic.user.member.resource.service.MemberResourceService;
import com.magic.user.service.MemberService;
import com.magic.user.vo.UserCondition;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 1:32
 */
@Service("memberResourceService")
public class MemberResourceServiceImpl implements MemberResourceService {

    @Resource(name = "memberService")
    private MemberService memberService;

    @Override
    public Page<User> findByPage(UserCondition userCondition) {
        return memberService.findByPage(userCondition);
    }
}
