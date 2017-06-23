package com.magic.user.service;

import com.magic.user.entity.AgentReview;
import com.magic.user.storage.AgentReviewDbService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/9
 * Time: 19:37
 */
@Service("agentReviewService")
public class AgentReviewServiceImpl implements AgentReviewService {

    @Resource(name = "agentReviewDbService")
    private AgentReviewDbService agentReviewDbService;

    @Override
    public boolean add(AgentReview agentReview) {
        Long result = agentReviewDbService.insert(agentReview);
        return !(result == null || result <=0 );
    }
}
