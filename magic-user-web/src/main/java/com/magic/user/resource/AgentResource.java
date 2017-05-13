package com.magic.user.resource;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.auth.Access;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.user.bean.UserCondition;
import com.magic.user.po.DownLoadFile;
import com.magic.user.resource.service.AgentResourceService;
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
 * Date: 2017/5/3
 * Time: 15:47
 */
@Controller
@RequestMapping(value = "/v1/agent")
public class AgentResource {


    @Resource(name = "agentResourceService")
    private AgentResourceService agentResourceService;

    /**
     * @param condition 检索条件
     * @param page      当前页
     * @param count     每页数据量
     * @return
     * @Doc 代理列表
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String list(
            @RequestParam(name = "condition", required = false) String condition,
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "count", required = false, defaultValue = "10") Integer count
    ) {

        UserCondition userCondition = JSONObject.parseObject(condition, UserCondition.class);
        String result = agentResourceService.findByPage(userCondition, page, count);
        return result;
    }

    /**
     * @param condition 检索条件
     * @return
     * @Doc 代理列表导出
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/list/export", method = RequestMethod.GET)
    @ResponseBody
    public void listExport(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name = "condition", required = false) String condition
    ) throws IOException {
        RequestContext rc = RequestContext.getRequestContext();
        DownLoadFile downLoadFile = agentResourceService.agentListExport(rc, condition);
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
     * @param holder       所属股东ID
     * @param account      代理账号
     * @param password     登录密码
     * @param realname     真实姓名
     * @param telephone    手机号码
     * @param bankCardNo   银行卡号
     * @param email        电子邮箱
     * @param returnScheme 返佣方案
     * @param adminCost    行政成本
     * @param feeScheme    手续费
     * @param domain       域名设置
     * @param discount     优惠扣除  默认1 1不选 2勾选
     * @param cost         反水成本  默认1 1不选 2勾选
     * @return
     * @Doc
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String add(
            HttpServletRequest request,
            @RequestParam(name = "holder") Long holder,
            @RequestParam(name = "account") String account,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "realname") String realname,
            @RequestParam(name = "telephone") String telephone,
            @RequestParam(name = "bankCardNo") String bankCardNo,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "returnScheme") Integer returnScheme,
            @RequestParam(name = "adminCost") Integer adminCost,
            @RequestParam(name = "feeScheme") Integer feeScheme,
            @RequestParam(name = "domain", required = false) String[] domain,
            @RequestParam(name = "discount", required = false, defaultValue = "1") Integer discount,
            @RequestParam(name = "cost", required = false, defaultValue = "1") Integer cost
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        StringBuffer url = request.getRequestURL();
        String sourceUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append("/").toString();
        return agentResourceService.add(rc, request, holder, account, password, realname, telephone, bankCardNo, email, returnScheme, adminCost, feeScheme, domain, discount, cost);
    }

    /**
     * @param id 代理ID
     * @return
     * @Doc 代理详情
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public String detail(
            @RequestParam(name = "id") Long id
    ) {

        return agentResourceService.getDetail(RequestContext.getRequestContext(), id);
    }

    /**
     * @param id       代理ID
     * @param password 密码
     * @return
     * @Doc 代理密码重置
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/password/reset", method = RequestMethod.POST)
    @ResponseBody
    public String passwordRest(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "password") String password

    ) {
        return agentResourceService.resetPwd(RequestContext.getRequestContext(), id, password);
    }

    /**
     * @param id         代理ID
     * @param realname   姓名
     * @param telephone  手机号
     * @param email      电子邮箱
     * @param bankCardNo 银行卡号
     * @param status     当前状态
     * @return
     * @Doc 代理资料修改
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
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

        return agentResourceService.update(RequestContext.getRequestContext(), id, realname, telephone, email, bankCardNo, bank);
    }

    /**
     * @param id           代理ID
     * @param returnScheme 退佣方案ID
     * @param adminCost    行政成本ID
     * @param feeScheme    手续费方案ID
     * @return
     * @Doc 代理参数配置修改
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/settings", method = RequestMethod.POST)
    @ResponseBody
    public String settings(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "returnScheme", required = false, defaultValue = "-1") Integer returnScheme,
            @RequestParam(name = "adminCost", required = false, defaultValue = "-1") Integer adminCost,
            @RequestParam(name = "feeScheme", required = false, defaultValue = "-1") Integer feeScheme
    ) {
        return agentResourceService.updateAgentConfig(RequestContext.getRequestContext(), id, returnScheme, adminCost, feeScheme);
    }

    /**
     * @param account    代理账号
     * @param password   密码
     * @param realname   真实姓名
     * @param telephone  电话
     * @param email      电子邮箱
     * @param bankCardNo 银行卡
     * @return
     * @Doc 代理申请--前端页面
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    @ResponseBody
    public String apply(
            HttpServletRequest request,
            @RequestParam(name = "account") String account,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "realname") String realname,
            @RequestParam(name = "telephone") String telephone,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "bankCardNo") String bankCardNo
    ) {
        return agentResourceService.agentApply(RequestContext.getRequestContext(), request, account, password, realname, email, telephone, bankCardNo);
    }

    /**
     * @param account 账号
     * @param status  状态
     * @param page    当前页
     * @param count   每页数据量
     * @return
     * @Doc 新增代理审核列表
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/review/list", method = RequestMethod.GET)
    @ResponseBody
    public String reviewList(
            @RequestParam(name = "account", required = false) String account,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "count", required = false, defaultValue = "10") Integer count
    ) {

        return agentResourceService.agentApplyList(RequestContext.getRequestContext(), account, status, page, count);
    }

    /**
     * @param account 账号
     * @param status  状态
     * @return
     * @Doc 新增代理审核列表导出
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/review/list/export", method = RequestMethod.GET)
    @ResponseBody
    public String reviewListExport(
            @RequestParam(name = "account", required = false) String account,
            @RequestParam(name = "status", required = false) Integer status
//            股东ID（从RequestContext中获取股东ID）
    ) {
        return "";
    }

    /**
     * @param id 代理审核id
     * @return
     * @Doc 代理审核基础信息    --代理审核页
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/detail/simple", method = RequestMethod.GET)
    @ResponseBody
    public String listExport(
            @RequestParam(name = "id") Long id
    ) {
        return agentResourceService.agentApplyInfo(RequestContext.getRequestContext(), id);
    }

    /**
     * @param id           代理申请ID
     * @param reviewStatus 审核结果 2：通过 3拒绝，如果审核通过，则需要填写后续字段
     * @param holder       所属股东ID
     * @param realname     真实姓名
     * @param telephone    手机号码
     * @param bankCardNo   银行卡号
     * @param email        电子邮箱
     * @param returnScheme 返佣方案
     * @param adminCost    行政成本
     * @param feeScheme    手续费
     * @param domain       域名设置
     * @param discount     优惠扣除 默认1 1不选 2勾选
     * @param cost         返水成本 默认1 1不选 2勾选
     * @return
     * @Doc 代理审核/拒绝
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/review", method = RequestMethod.POST)
    @ResponseBody
    public String review(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "reviewStatus") Integer reviewStatus,
            @RequestParam(name = "holder", required = false) Long holder,
            @RequestParam(name = "realname", required = false) String realname,
            @RequestParam(name = "telephone", required = false) String telephone,
            @RequestParam(name = "bankCardNo", required = false) String bankCardNo,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "returnScheme", required = false) Integer returnScheme,
            @RequestParam(name = "adminCost", required = false) Integer adminCost,
            @RequestParam(name = "feeScheme", required = false) Integer feeScheme,
            @RequestParam(name = "domain", required = false) String[] domain,
            @RequestParam(name = "discount", required = false, defaultValue = "1") Integer discount,
            @RequestParam(name = "cost", required = false, defaultValue = "1") Integer cost

    ) {
        return agentResourceService.agentReview(RequestContext.getRequestContext(), id, reviewStatus, holder, realname, telephone, bankCardNo, email, returnScheme, adminCost, feeScheme, domain, discount, cost);
    }

    /**
     * @param id     代理ID
     * @param status 状态 1 启用 2禁用
     * @return
     * @Doc 启用禁用代理
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/disable", method = RequestMethod.POST)
    @ResponseBody
    public String review(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "status") Integer status

    ) {
        return agentResourceService.disable(RequestContext.getRequestContext(), id, status);
    }


}
