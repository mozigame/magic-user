package com.magic.user.storage;

import com.magic.api.commons.atlas.core.BaseDao;
import com.magic.user.dao.AgentApplyDao;
import com.magic.user.entity.AgentApply;
import com.magic.user.entity.User;
import com.magic.user.storage.base.BaseDbServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:41
 */
@Service("agentApplyDbService")
public class AgentApplyDbService extends BaseDbServiceImpl<AgentApply, Long> {

    @Resource
    private AgentApplyDao agentApplyDao;

    @Override
    public BaseDao<AgentApply, Long> getDao() {
        return agentApplyDao;
    }
}
