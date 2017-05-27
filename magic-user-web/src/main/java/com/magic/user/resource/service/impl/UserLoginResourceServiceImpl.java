package com.magic.user.resource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.core.tools.MauthUtil;
import com.magic.api.commons.mq.Producer;
import com.magic.api.commons.mq.api.Topic;
import com.magic.api.commons.tools.IPUtil;
import com.magic.config.vo.OwnerInfo;
import com.magic.user.constants.UserContants;
import com.magic.user.entity.Login;
import com.magic.user.entity.User;
import com.magic.user.enums.AccountStatus;
import com.magic.user.enums.DeleteStatus;
import com.magic.user.enums.LoginType;
import com.magic.user.exception.UserException;
import com.magic.user.resource.service.UserLoginResourceService;
import com.magic.user.service.*;
import com.magic.user.service.dubbo.DubboOutAssembleServiceImpl;
import com.magic.user.util.PasswordCapture;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/27
 * Time: 15:05
 */
@Service("userLoginResourceService")
public class UserLoginResourceServiceImpl implements UserLoginResourceService {


    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "loginService")
    private LoginService loginService;
    @Resource
    private AccountIdMappingService accountIdMappingService;
    @Resource
    private DubboOutAssembleServiceImpl dubboOutAssembleService;
    @Resource
    private Producer producer;

    /**
     * {@inheritDoc}
     *
     * @param rc
     * @param agent
     * @param url
     * @param username
     * @param password
     * @param code
     * @return
     */
    @Override
    public String login(RequestContext rc, String agent, String url, String username, String password, String code) {
        //todo
//        if (!checkLoginReq(username, password)) {
//            throw UserException.ILLEGAL_PARAMETERS;
//        }
        OwnerInfo ownerInfo = dubboOutAssembleService.getOwnerInfoByDomain(url);
        if (ownerInfo == null || ownerInfo.getOwnerId() < 0) {
            throw UserException.ILLEGAL_SOURCE_URL;
        }
        long userId = accountIdMappingService.getUid(ownerInfo.getOwnerId(), username);
        if (userId <= 0) {
            throw UserException.USERNAME_NOT_EXIST;
        }
        //todo 从redis里面获取ownerId_username下的验证码，如果存在验证码，与code对比，如果相符，进行下一步操作
        String proCode = "";
//        if (!proCode.equals(code)) {
//            throw UserException.PROCODE_ERROR;
//        }
        User loginUser = userService.get(userId);
        if (loginUser == null) {
            throw UserException.ILLEGAL_USER;
        }
        if (loginUser.getStatus() == AccountStatus.disable
                || loginUser.getIsDelete() == DeleteStatus.del) {
            throw UserException.ILLEGAL_USER;
        }
        Login loginInfo = loginService.get(userId);
        if (loginInfo == null) {
            throw UserException.ILLEGAL_USER;
        }
        if (!loginInfo.getPassword().equals(PasswordCapture.getSaltPwd(password))) {
            throw UserException.ILLEDGE_USERNAME_PASSWORD;
        }

        int ip = IPUtil.ipToInt(rc.getIp());
        sendLoginHistory(userId, System.currentTimeMillis(), ip, LoginType.login, agent);
        String token = MauthUtil.createOld(userId, System.currentTimeMillis());
        JSONObject result = new JSONObject();
        result.put("token", token);
        result.put("userId", userId);
        return result.toJSONString();
    }

    /**
     * {@inheritDoc}
     *
     * @param rc
     * @param agent
     * @param username
     * @return
     */
    @Override
    public String logout(RequestContext rc, String agent, String username) {
        if (username == null || username.length() < 6 || username.length() > 16) {
            throw UserException.ILLEGAL_PARAMETERS;
        }
        User user = userService.get(rc.getUid());
        if (user == null) {
            throw UserException.ILLEGAL_USER;
        }
        long userId = accountIdMappingService.getUid(user.getOwnerId(), username);
        if (userId <= 0) {
            throw UserException.USERNAME_NOT_EXIST;
        }
        sendLoginHistory(userId, System.currentTimeMillis(), IPUtil.ipToInt(rc.getIp()), LoginType.logout, agent);
        return UserContants.EMPTY_STRING;
    }

    @Override
    public String verify(RequestContext rc) {
        Login login = loginService.get(rc.getUid());
        if (login == null || login.getStatus() != LoginType.login) {
            throw UserException.VERIFY_FAIL;
        }
        return "{\"username\":\"" + login.getUsername() + "\"}";
    }

    /**
     * @param username
     * @param password
     * @return
     * @Doc 验证登陆参数合法性
     */
    private boolean checkLoginReq(String username, String password) {
        if (username.length() >= 6 && username.length() <= 16 && password.length() == 32) {
            return true;
        }
        return false;
    }

    /**
     * @param userId
     * @param createTime
     * @param requestIp
     * @param loginType
     * @param platform
     * @Doc 发送用户登录历史到mq
     */
    private void sendLoginHistory(Long userId, Long createTime, Integer requestIp, LoginType loginType, String platform) {
        JSONObject object = new JSONObject();
        object.put("userId", userId);
        object.put("createTime", createTime);
        object.put("requestIp", requestIp);
        object.put("loginType", loginType.value());
        object.put("platform", platform);
        if (loginType == LoginType.login) {
            producer.send(Topic.USER_LOGIN_SUCCESS, userId + "", object.toJSONString());
        }
        if (loginType == LoginType.logout) {
            producer.send(Topic.USER_LOGOUT_SUCCESS, userId + "", object.toJSONString());
        }
    }
}