package com.magic.user.service;

import com.magic.user.entity.AgentReview;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:23
 */
public interface AgentReviewService {

    /**
     * @Doc 添加代理审核信息
     * @param agentReview
     * @return
     */
    boolean add(AgentReview agentReview);
}
