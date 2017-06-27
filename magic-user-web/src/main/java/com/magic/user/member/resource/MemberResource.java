package com.magic.user.member.resource;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.auth.Access;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.core.tools.HeaderUtil;
import com.magic.user.constants.UserContants;
import com.magic.user.member.resource.service.MemberResourceServiceImpl;
import com.magic.user.po.DownLoadFile;
import com.magic.user.po.RegisterReq;
import com.magic.user.util.CodeImageUtil;
import org.springframework.http.MediaType;
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
     * 会员注册
     *
     * @param request
     * @param response
     * @param proCode
     * @param username
     * @param password
     * @param paymentPassword
     * @param telephone
     * @param email
     * @param bank
     * @param realname
     * @param bankCardNo
     * @param bankDeposit
     * @param province
     * @param city
     * @param weixin
     * @param qq
     * @return
     */
    @Access(type = Access.AccessType.PUBLIC)
    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String register(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name = "proCode", required = false, defaultValue = "") String proCode,
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "password", required = true) String password,
            @RequestParam(name = "paymentPassword", required = false, defaultValue = "") String paymentPassword,
            @RequestParam(name = "telephone", required = false, defaultValue = "") String telephone,
            @RequestParam(name = "email", required = false, defaultValue = "") String email,
            @RequestParam(name = "bank", required = false, defaultValue = "") String bank,
            @RequestParam(name = "realname", required = false, defaultValue = "") String realname,
            @RequestParam(name = "bankCardNo", required = false, defaultValue = "") String bankCardNo,
            @RequestParam(name = "bankDeposit", required = false, defaultValue = "") String bankDeposit,
            @RequestParam(name = "province", required = false, defaultValue = "") String province,
            @RequestParam(name = "city", required = false, defaultValue = "") String city,
            @RequestParam(name = "weixin", required = false, defaultValue = "") String weixin,
            @RequestParam(name = "qq", required = false, defaultValue = "") String qq,
            @RequestParam(name = "code", required = true) String code

    ) {
        RequestContext rc = RequestContext.getRequestContext();
        //获取域名
        String url = rc.getOrigin();
        RegisterReq req = assembleRegister(proCode, username, password, paymentPassword, telephone, email, bank, realname, bankCardNo, bankDeposit, province, city, weixin, qq);
        return memberServiceResource.memberRegister(rc, url, req, code);
    }

    /**
     * 组装注册请求数据
     *
     * @param proCode
     * @param username
     * @param password
     * @param paymentPassword
     * @param telephone
     * @param email
     * @param bank
     * @param realname
     * @param bankCardNo
     * @param bankDeposit
     * @param province
     * @param city
     * @param weixin
     * @param qq
     * @return
     */
    private RegisterReq assembleRegister(String proCode, String username, String password, String paymentPassword, String telephone, String email, String bank, String realname, String bankCardNo, String bankDeposit, String province, String city, String weixin, String qq) {
        RegisterReq req = new RegisterReq();
        req.setProCode(proCode);
        req.setUsername(username);
        req.setPassword(password);
        req.setPaymentPassword(paymentPassword);
        req.setTelephone(telephone);
        req.setEmail(email);
        req.setBank(bank);
        req.setRealname(realname);
        req.setBankCardNo(bankCardNo);
        req.setBankDeposit(bankDeposit);
        req.setProvince(province);
        req.setCity(city);
        req.setWeixin(weixin);
        req.setQq(qq);
        return req;
    }

    /**
     * 会员登陆
     *
     * @param request
     * @param response
     * @param username
     * @param password
     * @param code
     * @return
     */

    @Access(type = Access.AccessType.PUBLIC)
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String login(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "password", required = true) String password,
            @RequestParam(name = "code", required = true) String code

    ) {
        RequestContext rc = RequestContext.getRequestContext();
        //获取浏览器、操作系统名称等数据
        String agent = request.getHeader(HeaderUtil.USER_AGENT);
        //获取域名
        String url = rc.getOrigin();
        return memberServiceResource.memberLogin(rc, agent, url, username, password, code);
    }

    /**
     * 会员登陆 -- 无需验证
     *
     * @param request
     * @param response
     * @param username
     * @param password
     * @return
     */

    @Access(type = Access.AccessType.COOKIE)
    @RequestMapping(value = "/inner/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String login(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name = "username", required = true) String username,
            @RequestParam(name = "password", required = true) String password

    ) {
        RequestContext rc = RequestContext.getRequestContext();
        //获取浏览器、操作系统名称等数据
        String agent = request.getHeader(HeaderUtil.USER_AGENT);
        //获取域名
        String url = rc.getOrigin();
        return memberServiceResource.memberLogin(rc, agent, url, username, password);
    }

    /**
     * 密码重置
     *
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/password/reset", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String reset(
            @RequestParam(name = "oldPassword", required = true) String oldPassword,
            @RequestParam(name = "newPassword", required = true) String newPassword

    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.memberPasswordReset(rc, oldPassword, newPassword);
    }

    /**
     * 登陆注销
     *
     * @param username
     * @return
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/logout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String logout(
            @RequestParam(name = "username", required = true) String username
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.memberLogout(rc, username);
    }

    /**
     * 登陆授权校验
     *
     * @return
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/game/verify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String verify(
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.memberLoginVerify(rc);
    }

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
        ApiLogger.info("/member/list , condition :" + condition);
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.memberList(rc, condition, page, count);
    }
    /**
     * @param condition 检索条件
     * @return
     * @Doc 会员列表导出
     */
    @Access(type = Access.AccessType.PUBLIC)
    @RequestMapping(value = "/list/export", method = RequestMethod.GET)
    @ResponseBody
    public void listExport(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "condition", required = false, defaultValue = "{}") String condition
    ) throws IOException {
        RequestContext rc = RequestContext.getRequestContext();
        rc.setUid(userId);
        DownLoadFile downLoadFile = memberServiceResource.memberListExport(rc, condition);
        response.setCharacterEncoding("UTF-8");
        if (downLoadFile != null && downLoadFile.getContent() != null && downLoadFile.getContent().length > 0) {
            String contnetDisposition = "attachment;filename=";
            if (downLoadFile.getFilename() != null) {
                contnetDisposition += URLEncoder.encode(downLoadFile.getFilename(), "utf-8");
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

//
//    /**
//     * @param request  HttpServletRequest
//     * @param response HttpServletResponse
//     * @param filePath example "/filesOut/Download/mst.txt"
//     * @return
//     */
//    public static void FilesDownload_stream(HttpServletRequest request, HttpServletResponse response, String filename, byte [] content ) {
//        //get server path (real path)
//        try {
//            byte[] buffer = new byte[inputStream.available()];
//            response.reset();
//            // 先去掉文件名称中的空格,然后转换编码格式为utf-8,保证不出现乱码,这个文件名称用于浏览器的下载框中自动显示的文件名
//            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filenames.replaceAll(" ", "").getBytes("utf-8"), "iso8859-1"));
//            response.addHeader("Content-Length", "" + content.length);
//            OutputStream os = new BufferedOutputStream(response.getOutputStream());
//            response.setContentType("application/octet-stream");
//            os.write(buffer);// 输出文件
//            os.flush();
//            os.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * @param id 会员id
     * @return
     * @Doc 会员详情信息查询
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String detail(
            @RequestParam(name = "id", required = true) long id
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.memberDetails(rc, id);
    }


    /**
     * @param id 会员id
     * @return
     * @Doc 会员资金概况刷新
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/fund/refresh", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String detail(
            @RequestParam(name = "id", required = true) Long id
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.fundProfileRefresh(rc, id);
    }

    /**
     * @param id       会员ID
     * @param password 新密码
     * @return
     * @Doc 会员登录密码重置
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/back/password/reset", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/force/logout", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
     * @Doc 会员层级修改, 前端直接进行页面跳转
     */
    @Deprecated
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/level/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
     * @return
     * @Doc 会员层级列表
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/level/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String levelList(
            @RequestParam(name = "lock", required = false, defaultValue = "1") int lock
//            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
//            @RequestParam(name = "count", required = false, defaultValue = "10") int count
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.memberLevelList(rc, lock);
    }

    /**
     * @param lock 是否锁定分层  默认1 0：非锁定 1锁定
     * @return
     * @Doc 会员层级列表导出
     */
    @Access(type = Access.AccessType.PUBLIC)
    @RequestMapping(value = "/level/list/export", method = RequestMethod.GET)
    @ResponseBody
    public void levelListExport(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "lock", required = false, defaultValue = "1") int lock
    ) throws IOException {
        RequestContext rc = RequestContext.getRequestContext();
        rc.setUid(userId);
        DownLoadFile downLoadFile = memberServiceResource.memberLevelListExport(rc, lock);
        response.setCharacterEncoding("UTF-8");
        if (downLoadFile != null && downLoadFile.getContent() != null && downLoadFile.getContent().length > 0) {
            String contnetDisposition = "attachment;filename=";
            if (downLoadFile.getFilename() != null) {
                contnetDisposition += URLEncoder.encode(downLoadFile.getFilename(), "utf-8");
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
     * @return
     * @Doc 层级映射列表
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/level/list/simple", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String levelListSpecial() {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.levelListSimple(rc);
    }

    /**
     * @param condition 检索条件
     * @param page      当前页
     * @param count     每页数据量
     * @return
     * @Doc 某层级下会员列表
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/level/list/special", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @Access(type = Access.AccessType.PUBLIC)
    @RequestMapping(value = "/level/list/special/export", method = RequestMethod.GET)
    @ResponseBody
    public void levelListSpecialExport(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "condition") String condition
    ) throws IOException {
        RequestContext rc = RequestContext.getRequestContext();
        rc.setUid(userId);
        DownLoadFile downLoadFile = memberServiceResource.memberListExport(rc, condition);
        response.setCharacterEncoding("UTF-8");
        if (downLoadFile != null && downLoadFile.getContent() != null && downLoadFile.getContent().length > 0) {
            String contnetDisposition = "attachment;filename=";
            if (downLoadFile.getFilename() != null) {
                contnetDisposition += URLEncoder.encode(downLoadFile.getFilename(), "utf-8");
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
     *
     * @param id     会员id
     * @param status 1 启用 2禁用
     * @return
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/disable", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String statusUpdate(
            @RequestParam(name = "id", required = true) Long id,
            @RequestParam(name = "status", required = true) Integer status) {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.memberStatusUpdate(rc, id, status);
    }

    /**
     * @param condition 检索条件
     * @param page      当前页
     * @param count     每页数据量
     * @return
     * @Doc 在线会员列表
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/online/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String onlineList(
            @RequestParam(name = "condition", required = false, defaultValue = "{}") String condition,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "count", required = false, defaultValue = "10") int count
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.onlineList(rc, condition, page, count);
    }

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     * @Doc 在线会员列表导出
     */
    @Access(type = Access.AccessType.PUBLIC)
    @RequestMapping(value = "/online/listExport", method = RequestMethod.GET)
    @ResponseBody
    public void onlineListExport(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name = "userId", required = true) Long userId,
//            @RequestParam(name = "loginStartTime", required = false) Long loginStartTime,
//            @RequestParam(name = "loginEndTime", required = false) Long loginEndTime,
//            @RequestParam(name = "registerStartTime", required = false) Long registerStartTime,
//            @RequestParam(name = "registerEndTime", required = false) Long registerEndTime
            @RequestParam(name = "condition", required = false, defaultValue = "{}") String condition
    ) throws IOException {
        RequestContext rc = RequestContext.getRequestContext();
        rc.setUid(userId);
        DownLoadFile downLoadFile = memberServiceResource.onlineListExport(rc,condition /*loginStartTime,loginEndTime,registerStartTime,registerEndTime*/);
        response.setCharacterEncoding("UTF-8");
        if(downLoadFile != null && downLoadFile.getContent() != null && downLoadFile.getContent().length > 0){
            String contnetDisposition = "attachment;filename=";
            if (downLoadFile.getFilename() != null) {
                contnetDisposition += URLEncoder.encode(downLoadFile.getFilename(), "utf-8");
                response.setHeader("Location", URLEncoder.encode(downLoadFile.getFilename(), "utf-8"));
            }
            response.setHeader("Content-Disposition", contnetDisposition);
            ServletOutputStream outputStream = response.getOutputStream();
            try {
                outputStream.write(downLoadFile.getContent());
            } catch (Exception e) {
                ApiLogger.error(String.format("export excel error. file: %s", downLoadFile.getContent()), e);
            } finally {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @return
     * @Doc 某层级下会员列表
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/online/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String online(
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.onlineCount(rc);
    }

    /**
     * @return
     * @Doc 获取会员中心详情信息
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/center/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String memberCenterDetail() {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.memberCenterDetail(rc);
    }

    /**
     * @return
     * @Doc 刷新会员的余额和未读消息
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/refresh", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String refresh() {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.getMemberInfo(rc);
    }

    /**
     * @return
     * @Doc 获取会员的交易记录
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/memberTradingRecord", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String memberTradingRecord(@RequestParam(name = "memberId", required = true) Long memberId) {
        RequestContext rc = RequestContext.getRequestContext();
        return memberServiceResource.memberTradingRecord(rc,memberId);
    }

    /**
     * 获取验证码
     *
     * @return
     */
    @Access(type = Access.AccessType.PUBLIC)
    @RequestMapping(value = "/code/get", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getCode(
                        @RequestParam(name = "width", required = false, defaultValue = "200") Integer width,
                        @RequestParam(name = "height", required = false, defaultValue = "80") Integer height) throws IOException {
        RequestContext rc = RequestContext.getRequestContext();
        ApiLogger.info("Origin:" + rc.getOrigin());
        String code = CodeImageUtil.generateVerifyCode(UserContants.VERIFY_CODE_LENGTH);
        String clientId = memberServiceResource.saveCode(rc, code);
        String base64Code = CodeImageUtil.outputImage(width, height, code);
        return assembleResult(clientId, base64Code);
    }

    /**
     * 组装返回数据
     * @param clientId
     * @param base64Code
     * @return
     */
    private String assembleResult(String clientId, String base64Code) {
        JSONObject object = new JSONObject();
        object.put("clientId", clientId);
        object.put("code", base64Code);
        return object.toJSONString();
    }
}
