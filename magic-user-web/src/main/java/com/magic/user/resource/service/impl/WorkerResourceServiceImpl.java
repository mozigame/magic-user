package com.magic.user.resource.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.model.PageBean;
import com.magic.api.commons.tools.DateUtil;
import com.magic.passport.po.SubAccount;
import com.magic.passport.service.dubbo.PassportDubboService;
import com.magic.service.java.UuidService;
import com.magic.tongtu.entity.Role;
import com.magic.tongtu.service.dubbo.PermitDubboService;
import com.magic.tongtu.vo.UserRoleVo;
import com.magic.user.constants.UserContants;
import com.magic.user.entity.Login;
import com.magic.user.entity.OwnerAccountUser;
import com.magic.user.entity.User;
import com.magic.user.enums.AccountStatus;
import com.magic.user.enums.AccountType;
import com.magic.user.exception.UserException;
import com.magic.user.resource.service.WorkerResourceService;
import com.magic.user.service.AccountIdMappingService;
import com.magic.user.service.LoginService;
import com.magic.user.service.UserService;
import com.magic.user.util.PasswordCapture;
import com.magic.user.vo.WorkerVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/21
 * Time: 22:57
 */
@Service("userResourceService")
public class WorkerResourceServiceImpl implements WorkerResourceService {

    @Resource
    private UserService userService;
    @Resource
    private UuidService uuidService;
    @Resource
    private PassportDubboService passportDubboService;
    @Resource
    private AccountIdMappingService accountIdMappingService;
    @Resource
    private LoginService loginService;
    @Resource
    private PermitDubboService permitDubboService;

    /**
     * {@inheritDoc}
     * @param account
     * @param realname
     * @return
     */
    @Override
    public String list(RequestContext rc, String account, String realname, Integer page, Integer count) {
        //todo 检查请求参数
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            throw UserException.ILLEGAL_USER;
        }
        long total = userService.getWorkerCount(operaUser.getOwnerId(), account, realname);
        if (total <= 0) {
            return JSON.toJSONString(assemblePageBean(page, count, total, null));
        }
        List<User> users = userService.findWorkers(operaUser.getOwnerId(), account, realname, page, count);
        List<Long> userIds = Lists.newArrayList();
        for (User user : users) {
            userIds.add(user.getUserId());
        }
        Map<Long, SubAccount> subAccountMap = passportDubboService.getSubLogins(userIds);
        //todo 调用dubbo
        Map<Long, UserRoleVo> userRoleVoMap = permitDubboService.getUsersRole(userIds);
        return JSON.toJSONString(assemblePageBean(page,count, total, assembleWorkerVoList(users, subAccountMap, userRoleVoMap)));
    }

    /**
     * 组装pageBean
     * @param page
     * @param count
     * @param total
     * @return
     */
    private PageBean assemblePageBean(Integer page, Integer count, Long total, List list) {
        PageBean pageBean = new PageBean();
        pageBean.setPage(page);
        pageBean.setCount(count);
        pageBean.setTotal(total);
        pageBean.setList(list);
        return pageBean;
    }

    /**
     * @组装子账号列表
     * @param users
     * @param subAccountMap
     * @param userRoleVoMap
     * @return
     */
    private List<WorkerVo> assembleWorkerVoList(List<User> users, Map<Long, SubAccount> subAccountMap, Map<Long, UserRoleVo> userRoleVoMap) {
        List<WorkerVo> WorkerVos = Lists.newArrayList();
        for (User user : users) {
            WorkerVo vo = new WorkerVo();
            vo.setId(user.getId());
            vo.setAccount(user.getUsername());
            vo.setRealname(user.getRealname());
            vo.setStatus(user.getStatus().value());
            vo.setShowStatus(user.getStatus().desc());
            vo.setCreateTime(DateUtil.formatDateTime(new Date(user.getRegisterTime()),DateUtil.formatDefaultTimestamp));
            SubAccount subAccount = subAccountMap.get(user.getUserId());
            if (subAccount != null) {
                vo.setLastLoginTime(DateUtil.formatDateTime(new Date(subAccount.getLastTime()),DateUtil.formatDefaultTimestamp));
            }
            UserRoleVo userRoleVo = userRoleVoMap.get(user.getUserId());
            if (userRoleVo != null) {
                vo.setRoleName(userRoleVo.getRoleName());
            }
        }
        return WorkerVos;
    }

    /**
     * 添加子账号
     * @param account
     * @param password
     * @param realname
     * @param roleId
     * @return
     */
    @Override
    public String add(RequestContext rc, String account, String password, String realname, Integer roleId) {
        //todo 检查参数
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            throw UserException.ILLEGAL_USER;
        }

        Long userId = uuidService.assignUid();
        //添加业主账号映射数据
        OwnerAccountUser ownerAccountUser = assembleOwnerAccount(operaUser.getOwnerId(), account, userId);
        if (accountIdMappingService.add(ownerAccountUser) <= 0) {
            throw UserException.REGISTER_FAIL;
        }
        //添加登录信息
        Login login = assembleWorkerLogin(userId, account, password);
        if (loginService.add(login) <= 0) {
            throw UserException.REGISTER_FAIL;
        }
        //添加账号信息
        User worker = assembleAddWorker(userId, operaUser.getOwnerId(), operaUser.getOwnerName(), AccountType.worker, account, realname);
        if (!userService.addWorker(worker)) {
            throw UserException.REGISTER_FAIL;
        }
        return "{\"id\":" + userId + "}";
    }

    /**
     * 组装业主id用户账号映射数据
     * @param ownerId
     * @param account
     * @param userId
     * @return
     */
    private OwnerAccountUser assembleOwnerAccount(Long ownerId, String account, Long userId) {
        OwnerAccountUser ownerAccountUser = new OwnerAccountUser();
        ownerAccountUser.setUserId(userId);
        ownerAccountUser.setAssemAccount(ownerId + UserContants.SPLIT_LINE +account);
        return ownerAccountUser;
    }

    /**
     * 组装添加的子账号对象
     * @param ownerId
     * @param ownerName
     * @param type
     * @param account
     * @param realname
     * @return
     */
    public User assembleAddWorker(Long userId, Long ownerId, String ownerName, AccountType type, String account, String realname) {
        User user = new User();
        user.setUserId(userId);
        user.setOwnerId(ownerId);
        user.setOwnerName(ownerName);
        user.setType(type);
        user.setUsername(account);
        user.setRealname(realname);
        return user;
    }

    /**
     * 组装添加的子账号登录信息
     * @param userId
     * @param account
     * @param password
     * @return
     */
    private Login assembleWorkerLogin(Long userId, String account, String password) {
        Login login = new Login();
        login.setUserId(userId);
        login.setUsername(account);
        login.setPassword(password);
        return login;
    }

    /**
     * 修改子账号
     * @param userId
     * @param realname
     * @param roleId
     * @return
     */
    @Override
    public String update(RequestContext rc, Long userId, String realname, Integer roleId) {
        //todo 检查参数
        User operaUser = userService.get(rc.getUid());
        if (operaUser == null) {
            throw UserException.ILLEGAL_USER;
        }
        User user = userService.get(userId);
        if (user == null) {
            throw UserException.ILLEGAL_USER;
        }
        user.setRealname(realname);
        //修改子账号
        if (!userService.update(user)) {
            throw UserException.USER_UPDATE_FAIL;
        }
        //修改角色
        //todo 调用dubbo
        if (!permitDubboService.updateUserRole(operaUser.getOwnerId(), userId, roleId)) {
            throw UserException.USER_UPDATE_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }



    /**
     * 修改状态
     * @param userId
     * @param status
     * @return
     */
    @Override
    public String updateStatus(RequestContext rc, Long userId, Integer status) {
        if (AccountStatus.parse(status) == null) {
            throw UserException.ILLEGAL_PARAMETERS;
        }
        User user = userService.get(userId);
        if (user == null) {
            throw UserException.ILLEGAL_USER;
        }
        if (user.getStatus().value() == status) {
            throw  UserException.USER_STATUS_UPDATE_FAIL;
        }
        //todo 调用dubbo修改状态,失败处理机制
        if (!userService.disable(user)) {
            throw UserException.USER_STATUS_UPDATE_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }

    /**
     * 获取详情
     * @param userId
     * @return
     */
    @Override
    public String detail(RequestContext rc, Long userId) {

        User user = userService.get(userId);
        if (user == null) {
            throw UserException.ILLEGAL_USER;
        }
        //todo 获取用户角色
        Role userRoleInfo = permitDubboService.getUserRoleInfo(userId);
        WorkerVo userVo = assembleUserDetail(user, userRoleInfo);
        return JSON.toJSONString(userVo);
    }

    /**
     * 组装子账号的详情
     * @param user
     * @param role
     * @return
     */
    private WorkerVo assembleUserDetail(User user, Role role) {
        WorkerVo vo = new WorkerVo();
        vo.setId(user.getUserId());
        vo.setAccount(user.getUsername());
        vo.setRealname(user.getRealname());
        if (role != null) {
            vo.setRoleId(role.getId());
            vo.setRoleName(role.getName());
        }
        return vo;
    }

    /**
     * 重置密码
     * @param userId
     * @param password
     * @return
     */
    @Override
    public String pwdReset(RequestContext rc, Long userId, String password) {
        //todo 检查参数
        if (!loginService.resetPassword(userId, PasswordCapture.getSaltPwd(password))) {
            throw UserException.PASSWORD_RESET_FAIL;
        }
        return UserContants.EMPTY_STRING;
    }

}