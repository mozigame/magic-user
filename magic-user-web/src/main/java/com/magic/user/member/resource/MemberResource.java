package com.magic.user.member.resource;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.auth.Access;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.user.member.resource.service.MemberResourceServiceImpl;
import com.magic.user.po.DownLoadFile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * User: joey
 * Date: 2017/5/1
 * Time: 19:05
 */
@Controller
@RequestMapping("/v1/member")
public class MemberResource {

    @Resource
    private MemberResourceServiceImpl memberServiceResource;

    /**
     * @param condition 检索条件
     * @param page      当前页
     * @param count     每页数据量
     * @return
     * @Doc 会员列表查询
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String list(
            @RequestParam(name = "condition", required = false, defaultValue = "{}") String condition,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "count", required = false, defaultValue = "10") int count
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.memberList(rc, condition, page, count);
    }

    /**
     * @param condition 检索条件
     * @return
     * @Doc 会员列表导出
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/list/export", method = RequestMethod.GET)
    @ResponseBody
    public void listExport(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name = "condition", required = false, defaultValue = "{}") String condition
    ) throws IOException {
        RequestContext rc = RequestContext.getRequestContext();
        DownLoadFile downLoadFile = memberServiceResource.memberListExport(rc, condition);
        response.setCharacterEncoding("UTF-8");
        if (downLoadFile != null && downLoadFile.getContent() != null && downLoadFile.getContent().length > 0) {
            String contnetDisposition = "attachment;filename=";
            if (downLoadFile.getFilename() != null) {
                contnetDisposition += URLEncoder.encode(contnetDisposition, "utf-8");
                response.setHeader("Location", URLEncoder.encode(downLoadFile.getFilename(), "utf-8"));
            }
            response.setHeader("Content-Disposition", contnetDisposition);
            ServletOutputStream outputStream = response.getOutputStream();
            try {
                outputStream.write(downLoadFile.getContent());
            }catch (Exception e){
                ApiLogger.error(String.format("export excel error. file: %s", downLoadFile.getContent()), e);
            }finally {
                outputStream.flush();
                outputStream.close();
            }
        }
    }

    /**
     * @param id 会员id
     * @return
     * @Doc 会员详情信息查询
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public String detail(
            @RequestParam(name = "id", required = true) long id
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.memberDetails(rc, id);
    }

    /**
     * @param id       会员ID
     * @param password 新密码
     * @return
     * @Doc 会员登录密码重置
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/back/password/reset", method = RequestMethod.POST)
    @ResponseBody
    public String backPasswordRest(
            @RequestParam(name = "id", required = true) long id,
            @RequestParam(name = "password", required = true) String password
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.passwordReset(rc, id, password);
    }

    /**
     * @param id 会员ID
     * @return
     * @Doc 会员强制下线
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/force/logout", method = RequestMethod.POST)
    @ResponseBody
    public String forceLogout(
            @RequestParam(name = "id") long id
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.logout(rc, id);
    }

    /**
     * @param id         会员ID
     * @param realname   姓名
     * @param telephone  手机号
     * @param email      电子邮箱
     * @param bankCardNo 银行卡号
     * @param status     当前状态
     * @return
     * @Doc 会员基础信息修改
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public String update(
            @RequestParam(name = "id", required = true) long id,
            @RequestParam(name = "realname", required = false, defaultValue = "") String realname,
            @RequestParam(name = "telephone", required = false, defaultValue = "") String telephone,
            @RequestParam(name = "email", required = false, defaultValue = "") String email,
            @RequestParam(name = "bankCardNo", required = false, defaultValue = "") String bankCardNo,
            @RequestParam(name = "status", required = false, defaultValue = "-1") int status
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.update(rc, id, realname, telephone, email, bankCardNo, status);
    }

    /**
     * @param id    会员ID
     * @param level 层级ID
     * @return
     * @Doc 会员层级修改
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/level/update", method = RequestMethod.POST)
    @ResponseBody
    public String levelUpdate(
            @RequestParam(name = "id", required = true) long id,
            @RequestParam(name = "level", required = true) int level
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.updateLevel(rc, id, level);
    }

    /**
     * @param lock  是否锁定分层  默认1 1：非锁定 2锁定
     * @param page  当前页
     * @param count 每页数据量
     * @return
     * @Doc 会员层级列表
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/level/list", method = RequestMethod.GET)
    @ResponseBody
    public String levelList(
            @RequestParam(name = "lock", required = false, defaultValue = "1") int lock,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "count", required = false, defaultValue = "10") int count
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.memberLevelList(rc, lock, page, count);
    }

    /**
     * @param lock 是否锁定分层  默认1 1：非锁定 2锁定
     * @return
     * @Doc 会员层级列表导出
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/level/list/export", method = RequestMethod.GET)
    @ResponseBody
    public void levelListExport(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name = "lock", required = false, defaultValue = "1") int lock
    ) throws IOException {
        RequestContext rc = RequestContext.getRequestContext();
        DownLoadFile downLoadFile = memberServiceResource.memberLevelListExport(rc, lock);
        response.setCharacterEncoding("UTF-8");
        if (downLoadFile != null && downLoadFile.getContent() != null && downLoadFile.getContent().length > 0) {
            String contnetDisposition = "attachment;filename=";
            if (downLoadFile.getFilename() != null) {
                contnetDisposition += URLEncoder.encode(contnetDisposition, "utf-8");
                response.setHeader("Location", URLEncoder.encode(downLoadFile.getFilename(), "utf-8"));
            }
            response.setHeader("Content-Disposition", contnetDisposition);
            ServletOutputStream outputStream = response.getOutputStream();
            try {
                outputStream.write(downLoadFile.getContent());
            } catch (Exception e) {
                ApiLogger.error(String.format("export excel error. file: %s", downLoadFile.getContent()), e);
            } finally {
                outputStream.flush();
                outputStream.close();
            }
        }
    }

    /**
     * @param condition 检索条件
     * @param page      当前页
     * @param count     每页数据量
     * @return
     * @Doc 某层级下会员列表
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/level/list/special", method = RequestMethod.GET)
    @ResponseBody
    public String levelListSpecial(
            @RequestParam(name = "condition") String condition,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "count", required = false, defaultValue = "10") int count
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.memberList(rc, condition, page, count);
    }

    /**
     * @param condition 检索条件
     * @return
     * @Doc 某层级系会员列表导出
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/level/list/special/export", method = RequestMethod.GET)
    @ResponseBody
    public void levelListSpecialExport(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name = "condition") String condition
    ) throws IOException {
        RequestContext rc = RequestContext.getRequestContext();
        DownLoadFile downLoadFile = memberServiceResource.memberListExport(rc, condition);
        response.setCharacterEncoding("UTF-8");
        if (downLoadFile != null && downLoadFile.getContent() != null && downLoadFile.getContent().length > 0) {
            String contnetDisposition = "attachment;filename=";
            if (downLoadFile.getFilename() != null) {
                contnetDisposition += URLEncoder.encode(contnetDisposition, "utf-8");
                response.setHeader("Location", URLEncoder.encode(downLoadFile.getFilename(), "utf-8"));
            }
            response.setHeader("Content-Disposition", contnetDisposition);
            ServletOutputStream outputStream = response.getOutputStream();
            try {
                outputStream.write(downLoadFile.getContent());
            } catch (Exception e) {
                ApiLogger.error(String.format("export excel error. file: %s", downLoadFile.getContent()), e);
            } finally {
                outputStream.flush();
                outputStream.close();
            }
        }
    }

    /**
     * 会员停用/启用
     * @param id 会员id
     * @param status 1 启用 2禁用
     * @return
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/disable", method = RequestMethod.GET)
    @ResponseBody
    public String statusUpdate(
            @RequestParam(name = "id", required = true) Long id,
            @RequestParam(name = "status", required = true) Integer status) {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.memberStatusUpdate(rc, id, status);
    }
}
