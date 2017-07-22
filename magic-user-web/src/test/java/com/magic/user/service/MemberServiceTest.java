package com.magic.user.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.magic.api.commons.core.context.RequestContext;
import com.magic.user.entity.User;
import com.magic.user.enums.AccountStatus;
import com.magic.user.enums.CurrencyType;
import com.magic.user.enums.GeneraType;

/**
 * User: joey
 * Date: 2017/5/6
 * Time: 18:29
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-application.xml"})
public class MemberServiceTest {

    //@Resource(name = "userService")
    private UserService userService;


    @Test
    public void insertUser() {
        User user = new User();
        user.setId(1L);
        user.setUserId(1L);
        user.setRealname("joey_update");
        user.setUsername("joey_userName_update");
        user.setCurrencyType(CurrencyType.CNY);
        user.setGender(GeneraType.male);
        user.setStatus(AccountStatus.enable);
        user.setEmail("ccaa@qq.com");

//        System.out.println(userService.addUser(user));
    }

    @Test
    public void updateUser() {
        User user = new User();
        user.setId(1L);
        user.setUserId(1L);
        user.setRealname("joey_update_____1");
        user.setUsername("joey_userName_update");
        user.setCurrencyType(CurrencyType.CNY);
        user.setGender(GeneraType.male);
        user.setStatus(AccountStatus.enable);
        user.setEmail("ccaa@qq.com-------");

//        System.out.println(userService.updateUser(user));
    }

    //MemberResourceServiceImpl memberResourceService;

    @Test
    public void getUser() {
        RequestContext c = RequestContext.getRequestContext();
        c.setUid(64);
        //String s = memberResourceService.memberCenterDetail(c);
        System.out.print(c);
    }
}
