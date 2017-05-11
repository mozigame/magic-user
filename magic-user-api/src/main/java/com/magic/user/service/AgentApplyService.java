package com.magic.user.service;

import com.magic.api.commons.model.Page;
import com.magic.user.entity.AgentApply;

import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:22
 */
public interface AgentApplyService {

    long add(AgentApply agentApply);

    Page<Map<String, Object>> findByPage(AgentApply agentApply, Page page);

    Map<String, Object> agentReviewInfo(Long applyId);

    int updateStatus(Long applyId, Integer status);

    AgentApply get(Long id);
}
