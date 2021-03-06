package com.magic.user.exception;

import com.magic.api.commons.core.exception.CommonException;
import com.magic.api.commons.core.exception.ExceptionFactor;

import javax.servlet.http.HttpServletResponse;

/**
 * UserException
 *
 * @author zj
 * @date 2017/5/8
 */
public class UserException extends ExceptionFactor {

    /**
     * 系统错误码
     */
    private static final int SystemCode = 3;

    /**
     * 非法请求参数
    */
    public static final CommonException ILLEGAL_PARAMETERS = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 0, HttpServletResponse.SC_OK, "illegal parameters!", "非法参数.");

    /**
     * 空列表
     */
    public static final CommonException EMPTY_RESULT_LIST = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 1, HttpServletResponse.SC_OK, "result is empty!", "无满足条件的查询结果.");

    /**
     * 非法会员
     */
    public static final CommonException ILLEGAL_MEMBER = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 2, HttpServletResponse.SC_OK, "illegal member!", "非法会员.");

    /**
     * 密码重置失败
     */
    public static final CommonException PASSWORD_RESET_FAIL = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 3, HttpServletResponse.SC_OK, "password reset fail!", "密码重置失败.");

    /**
     * 强制下线失败
     */
    public static final CommonException LOGOUT_FAIL = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 4, HttpServletResponse.SC_OK, "logout fail!", "注销失败.");

    /**
     * 会员信息更新失败
     */
    public static final CommonException MEMBER_UPDATE_FAIL = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 5, HttpServletResponse.SC_OK, "member info update fail!", "会员信息更新失败.");


    /**
     * 会员层级更新失败
     */
    public static final CommonException MEMBER_LEVEL_UPDATE_FAIL = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 6, HttpServletResponse.SC_OK, "member level update fail!", "会员层级更新失败.");

    /**
     * 会员层级条件更新失败
     */
    public static final CommonException MEMBER_LEVEL_CONDITION_UPDATE_FAIL = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 7, HttpServletResponse.SC_OK, "member level condition update fail!", "会员层级条件更新失败.");

    /**
     * 会员状态更新失败
     */
    public static final CommonException MEMBER_STATUS_UPDATE_FAIL = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 8, HttpServletResponse.SC_OK, "member status update fail!", "会员状态更新失败.");

    /**
     * 非法用户
     */
    public static final CommonException ILLEGAL_USER = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 9, HttpServletResponse.SC_OK, "illedge user!", "非法用户.");

    /**
     * 注册失败
     */
    public static final CommonException REGISTER_FAIL = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 10, HttpServletResponse.SC_OK, "register fail!", "注册失败.");

    /**
     * 用户名重复
     */
    public static final CommonException USERNAME_EXIST = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 11, HttpServletResponse.SC_OK, "username exist!", "用户名重复");

    /**
     * 用户名或密码非法
     */
    public static final CommonException ILLEDGE_USERNAME_PASSWORD = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 12, HttpServletResponse.SC_OK, "username or password illedge!", "用户名或密码非法.");

    /**
     * 验证码错误
     */
    public static final CommonException PROCODE_ERROR = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 13, HttpServletResponse.SC_OK, "code error!", "验证码错误.");

    /**
     * 登录失败
     */
    public static final CommonException MEMBER_LOGIN_FAIL = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 14, HttpServletResponse.SC_OK, "username or password error!", "用户名或密码错误.");

    /**
     * 用户名不存在
     */
    public static final CommonException USERNAME_NOT_EXIST = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 15, HttpServletResponse.SC_OK, "username not exist!", "用户名不存在.");

    /**
     * 密码错误
     */
    public static final CommonException PASSWORD_ERROR = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 16, HttpServletResponse.SC_OK, "password error!", "密码错误.");

    /**
     * 验证失败
     */
    public static final CommonException VERIFY_FAIL = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 17, HttpServletResponse.SC_OK, "login verify fail!", "验证失败.");

    /**
     * 用户信息更新失败
     */
    public static final CommonException USER_UPDATE_FAIL = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 18, HttpServletResponse.SC_OK, "user info update fail!", "用户信息更新失败.");

    /**
     * 用户状态更新失败
     */
    public static final CommonException USER_STATUS_UPDATE_FAIL = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 19, HttpServletResponse.SC_OK, "user status update fail!", "用户状态更新失败.");


    /**
     * 代理参数更新失败
     */
    public static final CommonException AGENT_CONFIG_UPDATE_FAIL = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 20, HttpServletResponse.SC_OK, "agent config update fail!", "代理参数更新失败.");


    /**
     * 代理申请添加失败
     */
    public static final CommonException AGENT_APPLY_ADD_FAIL = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 21, HttpServletResponse.SC_OK, "agent apply add fail!", "代理申请添加失败.");

    /**
     * 代理申请信息不存在
     */
    public static final CommonException AGENT_APPLY_NOT_EXIST = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 22, HttpServletResponse.SC_OK, "agent apply not exist!", "代理申请信息不存在.");

    /**
     * 代理审核失败
     */
    public static final CommonException AGENT_REVIEW_FAIL = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 23, HttpServletResponse.SC_OK, "agent review fail!", "代理审核失败.");

    /**
     * 无效的请求来源
     */
    public static final CommonException ILLEGAL_SOURCE_URL = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 24, HttpServletResponse.SC_OK, "illegal source url!", "无效的请求来源.");

    /**
     * 获取会员信息失败
     */
    public static final CommonException MEMBER_INFO_FAIL = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 25, HttpServletResponse.SC_OK, "member get info fail!", "获取会员信息失败.");
     /**
     * 会员账号被禁用
     */
    public static final CommonException MEMBER_ACCOUNT_DISABLED = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 25, HttpServletResponse.SC_OK, "member account is disabled!", "会员账号被禁用.");

    /**
     * 获取验证码失败
     */
    public static final CommonException GET_VERIFY_CODE_ERROR = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 26, HttpServletResponse.SC_OK, "get verify code fail!", "获取验证码失败.");

    /**
     * 验证码错误
     */
    public static final CommonException VERIFY_CODE_ERROR = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 27, HttpServletResponse.SC_OK, "verify code error!", "验证码错误.");

    /**
     * 验证码失效
     */
    public static final CommonException VERIFY_CODE_INVALID = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 28, HttpServletResponse.SC_OK, "verify code invalid!", "验证码失效.");

    /**
     * 用户名包含非法字符
     */
    public static final CommonException ILLEGAL_USERNAME = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 29, HttpServletResponse.SC_OK, "illegal username!", "用户名包含非法字符.");

    /**
     * 该账号已被冻结
     */
    public static final CommonException ACCOUNT_DISABLED = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 30, HttpServletResponse.SC_OK, "account is disabled!", "该账号已被冻结.");

    /**
     * 原密码错误
     */
    public static final CommonException OLD_PASSWORD_ERROR = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 31, HttpServletResponse.SC_OK, "old password error!", "原密码错误.");

    /**
     * 密码非法
     */
    public static final CommonException PASSWORD_ILLEDGE = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 32, HttpServletResponse.SC_OK, "password illedge!", "密码输入非法.");

    /**
     * 密码非法
     */
    public static final CommonException OLD_NEWPASSWORD_SAME = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 33, HttpServletResponse.SC_OK, "oldpassword is same with newpassword!", "新密码不能和原密码相同.");

    /**
     * 密码非法
     */
    public static final CommonException MEMBER_LOGIN_LOCKED = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 34, HttpServletResponse.SC_OK, "member info is locked!", "输入错误次数过多，账号已被锁定，请15分钟后再试.");

    /**
     * 注册次数过多
     */
    public static final CommonException REGISTER_TOO_FAST = new CommonException(
            CommonException.ERROR_LEVEL_SERVICE, SystemCode, 0, 35, HttpServletResponse.SC_OK, "member register too fast!", "注册次数太多，请稍后再试!");



}
