package com.magic.user.resource.service;

import com.magic.api.commons.core.context.RequestContext;
import com.magic.user.entity.AgentConfig;
import com.magic.user.po.DownLoadFile;
import com.magic.user.vo.AgentConfigVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 20:10
 */
public interface AgentResourceService {

    /**
     * @param userCondition
     * @return
     * @Doc 分页查询代理列表
     */
    String findByPage(RequestContext rc, String userCondition, int page, int count);

    /**
     * 会员层级列表导出
     *
     * @param rc        RequestContext
     * @param condition 查询条件
     * @return
     */
    DownLoadFile agentListExport(RequestContext rc, String condition);

    /**
     * @param rc
     * @param holder
     * @param account
     * @param password
     * @param realname
     * @param telephone
     * @param bankCardNo
     * @param email
     * @param returnScheme
     * @param adminCost
     * @param feeScheme
     * @param domain
     * @param discount
     * @param cost
     * @return
     * @Doc 添加
     */
    String add(RequestContext rc, HttpServletRequest request, Long holder, String account, String password, String realname, String telephone,
               String bankCardNo, String bank, String bankDeposit, String email, Integer returnScheme,
               Integer adminCost, Integer feeScheme, String domain, Integer discount, Integer cost,Long userLevel);

    /**
     * @param id
     * @return
     * @Doc 获取代理详情
     */
    String getDetail(RequestContext rc, Long id);


    /**
     * @param rc
     * @return
     * @Doc 获取代理拥有的域名
     */
    String getDomain(RequestContext rc, Long agentId);

    /**
     * @param id
     * @param password
     * @return
     * @Doc 重置密码
     */
    String resetPwd(RequestContext rc, Long id, String password);

    /**
     * @param rc
     * @param id
     * @param realname
     * @param telephone
     * @param email
     * @param bankCardNo
     * @param bank
     * @return
     * @Doc 代理基础信息修改
     */
    String update(RequestContext rc, Long id, String realname, String telephone, String email, String bankCardNo, String bank);


    /**
     * @param rc
     * @param agentId
     * @param returnScheme
     * @param adminCost
     * @param feeScheme
     * @return
     * @Doc 修改代理配置信息
     */
    String updateAgentConfig(RequestContext rc, Long agentId, Integer returnScheme, Integer adminCost, Integer feeScheme,Integer discount,Integer cost, String domain);

    /**
     * @param rc
     * @param account
     * @param password
     * @param realname
     * @param telephone
     * @param email
     * @param bankCardNo
     * @return
     * @Doc 添加代理申请
     */
    String agentApply(RequestContext rc, HttpServletRequest request,
            String account,
            String password,
            //String paymentPassword,
            String realname,
            String telephone,
            String email,
            String bankCardNo,
            String bank,
            String bankDeposit,
            String province,
            String city,
            String weixin,
            String qq);

    /**
     * @param rc
     * @param account
     * @param status
     * @param page
     * @param count
     * @return
     * @Doc 新增代理审核列表
     */
    String agentApplyList(RequestContext rc, String account, Integer status, Integer page, Integer count);

    /**
     * @Doc 代理审核列表导出
     * @param rc
     * @param status
     * @return
     */
    DownLoadFile reviewListExport(RequestContext rc,String account, Integer status);

    /**
     * @param rc
     * @param applyId
     * @return
     * @Doc 代理审核基础信息
     */
    String agentApplyInfo(RequestContext rc, Long applyId);

    /**
     * 查看代理审核信息
     * @param rc
     * @param applyId
     * @return
     */
    String reviewDetail(RequestContext rc, Long applyId);
    /**
     * @param rc
     * @param id
     * @param reviewStatus
     * @param holder
     * @param realname
     * @param telephone
     * @param bankCardNo
     * @param email
     * @param returnScheme
     * @param adminCost
     * @param feeScheme
     * @param domain
     * @param discount
     * @param cost
     * @return
     * @Doc 代理审核通过/拒绝
     */
    String agentReview(RequestContext rc, Long id, Integer reviewStatus, Long holder, String realname, String telephone,
                       String bankCardNo, String bank, String bankDeposit, String email, Integer returnScheme,
                       Integer adminCost, Integer feeScheme, String domain, Integer discount, Integer cost,Long userLevel);

    /**
     * @param rc
     * @param agentId
     * @param status
     * @return
     * @Doc 修改代理可用状态
     */
    String updateStatus(RequestContext rc, Long agentId, Integer status);

    /**
     * 获取所有的方案，成本，手续，域名
     * @param rc
     * @return
     */
    String allConfigs(RequestContext rc);
    /**
     * 获取 方案，成本，手续列表
     * @param rc
     * @return
     */
    String configs(RequestContext rc);

    /**
     * 资金概况
     * @param requestContext
     * @param id
     * @return
     */
    String fundProfileRefresh(RequestContext requestContext, Long id);

    /**
     *
     * @param requestContext
     * @return
     * @Doc 获取前端注册代理是的必填项
     */
    String getAgentRegisterMustParam(RequestContext requestContext);

    /**
     *
     * @param requestContext
     * @param id
     * @return
     * @Doc 查看域名
     */
    String queryDomains(RequestContext requestContext, Long id);

    /**
     *
     * @param requestContext
     * @param username
     * @return
     */
    String checkUsernameIsExists(RequestContext requestContext, String username);

    /**
     * 用于修复memberNumber的一致性
     * @param requestContext
     * @param agentIdList
     * @return
     */
    String repairMemberNumber(RequestContext requestContext, String agentIdList);

    /**
     * @param rc
     * @return
     * @Doc 获取域名下的代理信息
     */
    String getAgentConfigByDomain(RequestContext rc, String domain);
    /**
     * @param rc
     * @return
     * @Doc 模糊查询域名
     */
    String queryDomainInfoByDomain(RequestContext rc, String domain);
}
