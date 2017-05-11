package com.magic.user.storage;

import com.magic.api.commons.atlas.core.BaseDao;
import com.magic.user.dao.AgentDao;
import com.magic.user.entity.AgentConfig;
import com.magic.user.entity.User;
import com.magic.user.storage.base.BaseDbServiceImpl;
import com.magic.user.vo.UserCondition;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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

    public List<Map<String, Object>> findByPage(UserCondition condition) {
        try {
            return agentDao.find("findAgentList", new String[]{"offset", "limit"}, new Object[]{condition.getOffset(), condition.getLimit()});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public long getCount(UserCondition condition) {
        try {
            return agentDao.findCount("findAgentCount", null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

}
