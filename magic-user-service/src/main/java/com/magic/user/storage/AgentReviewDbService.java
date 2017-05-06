package com.magic.user.storage;

import com.magic.api.commons.atlas.core.BaseDao;
import com.magic.user.dao.AgentReviewDao;
import com.magic.user.entity.AgentReview;
import com.magic.user.entity.User;
import com.magic.user.storage.base.BaseDbServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:45
 */
@Service("agentReviewDbService")
public class AgentReviewDbService extends BaseDbServiceImpl<AgentReview, Long> {
    @Resource
    private AgentReviewDao agentReviewDao;

    @Override
    public BaseDao<AgentReview, Long> getDao() {
        return agentReviewDao;
    }
}
