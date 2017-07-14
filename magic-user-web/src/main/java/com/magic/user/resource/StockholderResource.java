package com.magic.user.resource;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.auth.Access;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.user.enums.AccountStatus;
import com.magic.user.exception.UserException;
import com.magic.user.po.DownLoadFile;
import com.magic.user.resource.service.StockResourceService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * User: joey
 * Date: 2017/5/3
 * Time: 14:45
 */
@Controller
@RequestMapping("/v1/stockholder")
public class StockholderResource {


    @Resource(name = "stockResourceService")
    private StockResourceService stockResourceService;

    /**
     * @return
     * @Doc 股东列表
     */
    @Access(type = Access.AccessType.RESOURCE)
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String list() {
        return stockResourceService.findAllStock(RequestContext.getRequestContext());
    }

    /**
     * @return
     * @Doc 股东列表导出
     */
    @Access(type = Access.AccessType.PUBLIC)
    @RequestMapping(value = "/list/export", method = RequestMethod.GET)
    @ResponseBody
    public void listExport(
            HttpServletResponse response,
            @RequestParam(name = "userId") Long userId
    ) throws IOException {
        RequestContext rc = RequestContext.getRequestContext();
        rc.setUid(userId);
        DownLoadFile downLoadFile = stockResourceService.listExport(rc);
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
     * @Doc 所属股东列表
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/list/simple", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String listSimple() {
        RequestContext rc = RequestContext.getRequestContext();
        return stockResourceService.simpleList(rc);
    }

    /**
     * @param id 股东ID
     * @return
     * @Doc 股东详情
     */
    @Access(type = Access.AccessType.RESOURCE)
    @RequestMapping(value = "/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String detail(
            @RequestParam(name = "id") Long id
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return stockResourceService.getStockDetail(rc, id);
    }


    /**
     * @param id       股东ID
     * @param password 新密码
     * @return
     * @Doc 股东密码重置
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/password/reset", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String passwordReset(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "password") String password
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return stockResourceService.updatePwd(rc, id, password);
    }

    /**
     * @param id         股东ID
     * @param realname   真实姓名
     * @param telephone  手机号
     * @param email      电子邮箱
     * @param bankCardNo 银行卡号
     * @param status     当前状态
     * @return
     * @Doc 股东基础信息修改
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String update(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "realname", required = false) String realname,
            @RequestParam(name = "telephone", required = false) String telephone,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "bankCardNo", required = false) String bankCardNo,
            @RequestParam(name = "bank", required = false) String bank,
            @RequestParam(name = "status", required = false) Integer status
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return stockResourceService.update(rc, id, realname, telephone, email, bankCardNo, bank, status);
    }

    /**
     * @param account      股东账号
     * @param password     登录密码
     * @param realname     真实姓名
     * @param telephone    手机号
     * @param currencyType 币种
     * @param email        电子邮箱
     * @param sex          性别
     * @return
     * @Doc 新增股东
     */
    @Access(type = Access.AccessType.RESOURCE)
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String add(
            @RequestParam(name = "account") String account,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "realname") String realname,
            @RequestParam(name = "bankCardNo") String bankCardNo,
            @RequestParam(name = "bankDeposit") String bankDeposit,
            @RequestParam(name = "bank") String bank,
            @RequestParam(name = "telephone") String telephone,
            @RequestParam(name = "email", required = false, defaultValue = "") String email,
            @RequestParam(name = "currencyType", required = false, defaultValue = "1") Integer currencyType,
            @RequestParam(name = "sex", required = false, defaultValue = "1") Integer sex
    ) {


        return stockResourceService.add(RequestContext.getRequestContext(), account, password, realname, bankCardNo, bankDeposit, bank, telephone, currencyType, email, sex);
    }

    /**
     * @param id     股东ID
     * @param status 状态,1、启用，2、禁用
     * @return
     * @Doc 启用停用股东
     */
    @Access(type = Access.AccessType.RESOURCE)
    @RequestMapping(value = "/disable", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String disable(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "status") Integer status
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        if (AccountStatus.parse(status) == null) {
            throw UserException.ILLEGAL_PARAMETERS;
        }
        return stockResourceService.updateStatus(rc, id, status);
    }



}
