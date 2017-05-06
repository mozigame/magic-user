package com.magic.user.storage;

import com.magic.api.commons.atlas.core.BaseDao;
import com.magic.user.dao.AgentConfigDao;
import com.magic.user.entity.AgentConfig;
import com.magic.user.entity.User;
import com.magic.user.storage.base.BaseDbServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:41
 */
@Service("agentConfigDbService")
public class AgentConfigDbService extends BaseDbServiceImpl<AgentConfig, Long> {

    @Resource
    private AgentConfigDao agentConfigDao;

    @Override
    public BaseDao<AgentConfig, Long> getDao() {
        return agentConfigDao;
    }
}
