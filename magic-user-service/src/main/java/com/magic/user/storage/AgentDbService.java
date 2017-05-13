package com.magic.user.storage;

import com.magic.api.commons.atlas.core.BaseDao;
import com.magic.user.dao.AgentDao;
import com.magic.user.entity.User;
import com.magic.user.storage.base.BaseDbServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 17:08
 */
@Service("agentDbService")
public class AgentDbService extends BaseDbServiceImpl<User, Long> {

    @Resource
    private AgentDao agentDao;

    @Override
    public BaseDao<User, Long> getDao() {
        return agentDao;
    }


}
