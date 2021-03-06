package com.magic.user.resource;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.auth.Access;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.user.po.DownLoadFile;
import com.magic.user.resource.service.AgentResourceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
 * Date: 2017/5/3
 * Time: 15:47
 */
@Controller
@RequestMapping(value = "/v1/agent")
public class AgentResource {

    private static final Logger logger = LogManager.getLogger(AgentResource.class);

    @Resource(name = "agentResourceService")
    private AgentResourceService agentResourceService;

    /**
     * @param condition 检索条件
     * @param page      当前页
     * @param count     每页数据量
     * @return
     * @Doc 代理列表
     */
    @Access(type = Access.AccessType.RESOURCE)
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String list(
            @RequestParam(name = "condition", required = false) String condition,
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "count", required = false, defaultValue = "10") Integer count
    ) {

        ApiLogger.info(condition);
        String result = agentResourceService.findByPage(RequestContext.getRequestContext(), condition, page, count);
        return result;
    }

    /**
     * @param condition 检索条件
     * @return
     * @Doc 代理列表导出
     */
    @Access(type = Access.AccessType.PUBLIC)
    @RequestMapping(value = "/list/export", method = RequestMethod.GET)
    @ResponseBody
    public void listExport(
            HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "condition", required = false, defaultValue = "{}") String condition
    ) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("listExport::userId = " + userId + ",condition = " + condition);
        }
        RequestContext rc = RequestContext.getRequestContext();
        rc.setUid(userId);
        DownLoadFile downLoadFile = agentResourceService.agentListExport(rc, condition);
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
     * @param holder          所属股东ID
     * @param account         代理账号
     * @param password        登录密码
     * @param realname        真实姓名
     * @param telephone       手机号码
     * @param bankCardNo      银行卡号
     * @param email           电子邮箱
     * @param returnScheme    返佣方案
     * @param adminCost       行政成本
     * @param feeScheme       手续费
     * @param domain          域名设置
     * @param discount        优惠扣除  默认1 1不选 2勾选
     * @param cost            反水成本  默认1 1不选 2勾选
     * @param registerOfferId 会员注册优惠
     * @return
     * @Doc 手动添加代理
     */
    @Access(type = Access.AccessType.RESOURCE)
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String add(
            HttpServletRequest request,
            @RequestParam(name = "holder") Long holder,
            @RequestParam(name = "account") String account,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "realname") String realname,
            @RequestParam(name = "telephone") String telephone,
            @RequestParam(name = "bankCardNo") String bankCardNo,
            @RequestParam(name = "bank") String bank,
            @RequestParam(name = "bankDeposit") String bankDeposit,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "returnScheme") Integer returnScheme,
            @RequestParam(name = "adminCost") Integer adminCost,
            @RequestParam(name = "feeScheme") Integer feeScheme,
            @RequestParam(name = "domain", required = false) String domain,
            @RequestParam(name = "discount", required = false, defaultValue = "1") Integer discount,
            @RequestParam(name = "cost", required = false, defaultValue = "1") Integer cost,
            @RequestParam(name = "userLevel", required = false, defaultValue = "-1") Long userLevel,  //新增用户层级id
            @RequestParam(name = "registerOfferId", required = false, defaultValue = "-1") Long registerOfferId  //会员注册优惠方案
    ) {
        RequestContext rc = RequestContext.getRequestContext();
        return agentResourceService.add(rc, request, holder, account, password, realname, telephone, bankCardNo, bank, bankDeposit, email, returnScheme, adminCost, feeScheme, domain, discount, cost, userLevel, registerOfferId);
    }

    /**
     * @param id 代理ID
     * @return
     * @Doc 资金概况刷新
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/fund/refresh", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String fundRefresh(
            @RequestParam(name = "id") Long id
    ) {
        return agentResourceService.fundProfileRefresh(RequestContext.getRequestContext(), id);
    }

    /**
     * @param id 代理ID
     * @return
     * @Doc 代理详情
     */
    @Access(type = Access.AccessType.RESOURCE)
    @RequestMapping(value = "/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String detail(
            @RequestParam(name = "id") Long id
    ) {
        return agentResourceService.getDetail(RequestContext.getRequestContext(), id);
    }

    /**
     * @param id 代理ID
     * @return
     * @Doc 获取代理下的域名
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/domain", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String domain(
            @RequestParam(name = "id") Long id
    ) {

        return agentResourceService.getDomain(RequestContext.getRequestContext(), id);
    }


    /**
     * @param id       代理ID
     * @param password 密码
     * @return
     * @Doc 代理密码重置
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/password/reset", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String update(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "realname", required = false) String realname,
            @RequestParam(name = "telephone", required = false) String telephone,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "bankCardNo", required = false) String bankCardNo,
            @RequestParam(name = "bank", required = false) String bank,
            @RequestParam(name = "status", required = false, defaultValue = "-1") Integer status
    ) {

        return agentResourceService.update(RequestContext.getRequestContext(), id, realname, telephone, email, bankCardNo, bank);
    }

    /**
     * @param id           代理ID
     * @param returnScheme 退佣方案ID
     * @param adminCost    行政成本ID
     * @param feeScheme    手续费方案ID
     * @param discount     优惠扣除 默认1 1不选 2勾选
     * @param cost         返水成本 默认1 1不选 2勾选
     * @param domains      域名,www.123.com,233.abc.com
     * @return
     * @Doc 代理参数配置修改
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/settings", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String settings(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "returnScheme", required = false, defaultValue = "-1") Integer returnScheme,
            @RequestParam(name = "adminCost", required = false, defaultValue = "-1") Integer adminCost,
            @RequestParam(name = "feeScheme", required = false, defaultValue = "-1") Integer feeScheme,
            @RequestParam(name = "discount", required = false, defaultValue = "-1") Integer discount,
            @RequestParam(name = "cost", required = false, defaultValue = "-1") Integer cost,
            @RequestParam(name = "domains", required = false, defaultValue = "") String domains,
            @RequestParam(name = "registerOfferId", required = false, defaultValue = "-1") Long registerOfferId

    ) {
        return agentResourceService.updateAgentConfig(RequestContext.getRequestContext(), id, returnScheme, adminCost, feeScheme, discount, cost, domains, registerOfferId);
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
    @Access(type = Access.AccessType.PUBLIC)
    @RequestMapping(value = "/apply", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String apply(
            HttpServletRequest request,
            @RequestParam(name = "username", required = true) String account,
            @RequestParam(name = "password", required = true) String password,
            // @RequestParam(name = "paymentPassword",required = true) String paymentPassword,
            @RequestParam(name = "realname", required = true) String realname,
            @RequestParam(name = "telephone", required = true) String telephone,
            @RequestParam(name = "email", required = false, defaultValue = "") String email,
            @RequestParam(name = "bankCardNo", required = true) String bankCardNo,
            @RequestParam(name = "bank", required = true) String bank,
            @RequestParam(name = "bankDeposit", required = true) String bankDeposit,
            @RequestParam(name = "province", required = false, defaultValue = "") String province,
            @RequestParam(name = "city", required = false, defaultValue = "") String city,
            @RequestParam(name = "weixin", required = false, defaultValue = "") String weixin,
            @RequestParam(name = "qq", required = false, defaultValue = "") String qq

    ) {
        return agentResourceService.agentApply(RequestContext.getRequestContext(), request, account, password,//paymentPassword,
                realname, telephone, email, bankCardNo, bank, bankDeposit, province, city, weixin, qq);
    }

    /**
     * @param username 代理账号
     * @Doc 代理申请--前端页面
     */
    @Access(type = Access.AccessType.PUBLIC)
    @RequestMapping(value = "/username/check", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String checkUserName(
            HttpServletRequest request,
            @RequestParam(name = "username", required = true) String username

    ) {
        return agentResourceService.checkUsernameIsExists(RequestContext.getRequestContext(), username);
    }


    /**
     * @param account 账号
     * @param status  状态
     * @param page    当前页
     * @param count   每页数据量
     * @return
     * @Doc 获取代理审核列表
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/review/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
     * @Doc 代理审核列表导出
     */
    @Access(type = Access.AccessType.PUBLIC)
    @RequestMapping(value = "/review/list/export", method = RequestMethod.GET)
    @ResponseBody
    public void reviewListExport(
            HttpServletResponse response,
            @RequestParam(name = "account", required = false) String account,
            @RequestParam(name = "status", required = false) Integer status,
            @RequestParam(name = "userId") Long userId
    ) throws IOException {
        RequestContext rc = RequestContext.getRequestContext();
        rc.setUid(userId);
        DownLoadFile downLoadFile = agentResourceService.reviewListExport(rc, account, status);
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
     * @param id 代理审核id
     * @return
     * @Doc 代理审核基础信息    --代理审核页
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/detail/simple", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String detailSimple(
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
     * @param bank         银行名称
     * @param bankDeposit  开户行
     * @param email        电子邮箱
     * @param returnScheme 返佣方案
     * @param adminCost    行政成本
     * @param feeScheme    手续费
     * @param domain       域名设置,用 "," 隔开，eg: www.123.com,www.222.com,www.bbb.com
     * @param discount     优惠扣除 默认1 1不选 2勾选
     * @param cost         返水成本 默认1 1不选 2勾选
     * @return
     * @Doc 代理审核/拒绝
     */
    @Access(type = Access.AccessType.RESOURCE)
    @RequestMapping(value = "/review", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String review(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "reviewStatus") Integer reviewStatus,
            @RequestParam(name = "holder", required = false) Long holder,
            @RequestParam(name = "realname", required = false) String realname,
            @RequestParam(name = "telephone", required = false) String telephone,
            @RequestParam(name = "bankCardNo", required = false) String bankCardNo,
            @RequestParam(name = "bank", required = false) String bank,
            @RequestParam(name = "bankDeposit", required = false) String bankDeposit,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "returnScheme", required = false, defaultValue = "-1") Integer returnScheme,
            @RequestParam(name = "adminCost", required = false, defaultValue = "-1") Integer adminCost,
            @RequestParam(name = "feeScheme", required = false, defaultValue = "-1") Integer feeScheme,
            @RequestParam(name = "domain", required = false) String domain,
            @RequestParam(name = "discount", required = false, defaultValue = "1") Integer discount,
            @RequestParam(name = "cost", required = false, defaultValue = "1") Integer cost,
            @RequestParam(name = "userLevel", required = false, defaultValue = "-1") Long userLevel,  //新增用户层级id
            @RequestParam(name = "registerOfferId", required = false, defaultValue = "-1") Long registerOfferId

    ) {
        //TODO 所有涉及审核的信息增加开户行信息、银行名称
        return agentResourceService.agentReview(RequestContext.getRequestContext(), id, reviewStatus, holder, realname, telephone, bankCardNo, bank, bankDeposit, email, returnScheme, adminCost, feeScheme, domain, discount, cost, userLevel, registerOfferId);
    }


    /**
     * @param id 代理审核id
     * @return
     * @Doc 查看代理审核信息
     */
    @Access(type = Access.AccessType.RESOURCE)
    @RequestMapping(value = "/review/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String reviewDetail(
            @RequestParam(name = "id") Long id
    ) {
        return agentResourceService.reviewDetail(RequestContext.getRequestContext(), id);
    }

    /**
     * @param id     代理ID
     * @param status 状态 1 启用 2禁用
     * @return
     * @Doc 启用禁用代理
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/disable", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String review(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "status") Integer status

    ) {
        return agentResourceService.updateStatus(RequestContext.getRequestContext(), id, status);
    }


    /**
     * @return
     * @Doc 获取 方案，成本，手续，域名 列表
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/all/configs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String allConfigs() {
        return agentResourceService.allConfigs(RequestContext.getRequestContext());
    }

    /**
     * @return
     * @Doc 查看域名
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/domains", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String queryDomains(@RequestParam(name = "id") Long id) {
        return agentResourceService.queryDomains(RequestContext.getRequestContext(), id);
    }

    /**
     * @return
     * @Doc 获取 方案，成本，手续列表
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/configs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String configs() {
        return agentResourceService.configs(RequestContext.getRequestContext());
    }

    /**
     * @return
     * @Doc 获取前端注册代理是的必填项
     */
    @Access(type = Access.AccessType.PUBLIC)
    @RequestMapping(value = "/getAgentRegisterMustParam", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getAgentRegisterMustParam() {
        return agentResourceService.getAgentRegisterMustParam(RequestContext.getRequestContext());
    }


    /**
     * @Doc 修复mongo agent member数据数据，内部使用接口
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/repair/member_number", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String repairMemberNumber(
            @RequestParam(name = "agentIdList", required = true) String agentIdList
    ) {
        String result = agentResourceService.repairMemberNumber(RequestContext.getRequestContext(), agentIdList);
        return result;
    }

    /**
     * @param domain
     * @return
     * @Doc 获取代理下的域名
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/getAgentByDomain", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getAgentByDomain(@RequestParam(name = "domain") String domain) {
        return agentResourceService.getAgentConfigByDomain(RequestContext.getRequestContext(), domain);
    }
    /**
     * @return
     * @Doc 查询域名
     */
    /*@Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/queryDomainInfoByDomain", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String queryDomain(String domain) {
        return agentResourceService.queryDomainInfoByDomain(RequestContext.getRequestContext(),domain);
    }*/
}
