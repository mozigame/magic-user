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

}
