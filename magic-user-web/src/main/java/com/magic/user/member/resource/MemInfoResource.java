package com.magic.user.member.resource;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.auth.Access;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.user.member.resource.service.InfoResourceServiceImpl;
import com.magic.user.po.DownLoadFile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
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
 * Date: 2017/5/3
 * Time: 17:13
 */
@Component
@RequestMapping("/v1/info")
public class MemInfoResource {

    @Resource
    private InfoResourceServiceImpl infoResourceService;

    /**
     * @param type    账号类型
     * @param account 账号名
     * @return
     * @Doc 资料查询
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String detail(
            @RequestParam(name = "type", required = true) Integer type,
            @RequestParam(name = "account", required = true) String account
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return infoResourceService.infoDetail(rc, type, account);
    }

    /**
     * @param id              账号ID
     * @param type            账号类型    1业主  2股东     3代理     4会员
     * @param realname        真实姓名
     * @param telephone       手机号码
     * @param email           邮箱
     * @param bankCardNo      银行卡号
     * @param bank            银行名称
     * @param bankDeposit     开户行
     * @param loginPassword   登录密码
     * @param paymentPassword 支付密码
     * @return
     * @Doc 资料修改
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/modify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String modify(
            @RequestParam(name = "id", required = true) Long id,
            @RequestParam(name = "type", required = true) Integer type,
            @RequestParam(name = "realname", required = false, defaultValue = "") String realname,
            @RequestParam(name = "telephone", required = false, defaultValue = "") String telephone,
            @RequestParam(name = "email", required = false, defaultValue = "") String email,
            @RequestParam(name = "bankCardNo", required = false, defaultValue = "") String bankCardNo,
            @RequestParam(name = "bank", required = false, defaultValue = "") String bank,
            @RequestParam(name = "bankDeposit", required = false, defaultValue = "") String bankDeposit,
            @RequestParam(name = "loginPassword", required = false, defaultValue = "") String loginPassword,
            @RequestParam(name = "paymentPassword", required = false, defaultValue = "") String paymentPassword
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return infoResourceService.modifyInfo(rc, id, type, realname, telephone, email, bankCardNo, bank, bankDeposit, loginPassword, paymentPassword);
    }

    /**
     * @param type    账号类型
     * @param account 账号名
     * @param page    当前页
     * @param count   每页数据量
     * @return
     * @Doc 修改记录查询
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/modify/list", method = RequestMethod.GET)
    @ResponseBody
    public String modifyList(
            @RequestParam(name = "type", required = false, defaultValue = "-1") Integer type,
            @RequestParam(name = "account", required = false, defaultValue = "") String account,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "count", required = false, defaultValue = "10") int count
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return infoResourceService.modifyList(rc, type, account, page, count);
    }

    /**
     * @param type    账号类型
     * @param account 账号名
     * @return
     * @Doc 修改记录导出
     */
    @Access(type = Access.AccessType.PUBLIC)
    @RequestMapping(value = "/modify/list/export", method = RequestMethod.GET)
    @ResponseBody
    public void modifyListExport(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name = "type", required = false, defaultValue = "-1") Integer type,
            @RequestParam(name = "account", required = false, defaultValue = "") String account
    ) throws IOException {
        RequestContext rc = RequestContext.getRequestContext();
        //todo
        rc.setUid(105094L);
        DownLoadFile downLoadFile = infoResourceService.modifyListExport(rc, type, account);
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


}
