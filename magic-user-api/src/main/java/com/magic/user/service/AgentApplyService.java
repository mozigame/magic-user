package com.magic.user.service;

import com.magic.api.commons.model.Page;
import com.magic.user.entity.AgentApply;
import com.magic.user.vo.AgentApplyVo;

import java.util.List;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:22
 */
public interface AgentApplyService {
    /**
     * @Doc 添加代理申请信息
     * @param agentApply
     * @return
     */
    long add(AgentApply agentApply);

    /**
     *  @Doc 分页查询代理申请数据
     * @param account
     * @param status
     * @param page
     * @param count
     * @return
     */
    List<AgentApplyVo> findByPage(Long ownerId, String account, Integer status, Integer page, Integer count);

    /**
     * @Doc 获取代理申请数据条数
     * @param account
     * @param status
     * @return
     */
    long getCount(Long ownerId, String account, Integer status);

    /**
     * @Doc 获取代理基础信息
     * @param applyId
     * @return
     */
    AgentApplyVo agentReviewInfo(Long applyId);

    /**
     * @Doc 修改代理审核状态
     * @param applyId
     * @param status
     * @return
     */
    int updateStatus(Long applyId, Integer status);

    /**
     * @Doc 获取代理申请信息
     * @param id
     * @return
     */
    AgentApply get(Long id);
}
