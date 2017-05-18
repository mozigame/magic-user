package com.magic.user.resource.service;

import com.magic.api.commons.core.context.RequestContext;
import com.magic.user.bean.AgentCondition;
import com.magic.user.po.DownLoadFile;

import javax.servlet.http.HttpServletRequest;

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
    String add(RequestContext rc, HttpServletRequest request, Long holder, String account, String password, String realname, String telephone, String bankCardNo, String email, Integer returnScheme,
               Integer adminCost, Integer feeScheme, String[] domain, Integer discount, Integer cost);

    /**
     * @param id
     * @return
     * @Doc 获取代理详情
     */
    String getDetail(RequestContext rc, Long id);

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
    String updateAgentConfig(RequestContext rc, Long agentId, Integer returnScheme, Integer adminCost, Integer feeScheme);

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
    String agentApply(RequestContext rc, HttpServletRequest request, String account, String password, String realname, String telephone, String email, String bankCardNo);

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
     * @param rc
     * @param applyId
     * @return
     * @Doc 代理审核基础信息
     */
    String agentApplyInfo(RequestContext rc, Long applyId);

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
                       String bankCardNo, String email, Integer returnScheme,
                       Integer adminCost, Integer feeScheme, String[] domain, Integer discount, Integer cost);

    /**
     * @param rc
     * @param agentId
     * @param status
     * @return
     * @Doc 修改代理可用状态
     */
    String updateStatus(RequestContext rc, Long agentId, Integer status);

    /**
     * @param rc
     * @param agent
     * @param url
     * @param username
     * @param password
     * @param code
     * @return
     * @Doc 用户登录
     */
    String login(RequestContext rc, String agent, String url, String username, String password, String code);

    /**
     * @param rc
     * @param username
     * @return
     * @Doc 用户注销
     */
    String logout(RequestContext rc, String agent, String username);
}
