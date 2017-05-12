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

    long add(AgentApply agentApply);

    List<AgentApplyVo> findByPage(String account, Integer status, Integer page, Integer count);

    long getCount(String account, Integer status);

    AgentApplyVo agentReviewInfo(Long applyId);

    int updateStatus(Long applyId, Integer status);

    AgentApply get(Long id);
}
