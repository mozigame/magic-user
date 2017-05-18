package com.magic.user.storage;

import com.magic.api.commons.atlas.core.BaseDao;
import com.magic.api.commons.model.Page;
import com.magic.user.dao.AgentApplyDao;
import com.magic.user.entity.AgentApply;
import com.magic.user.storage.base.BaseDbServiceImpl;
import com.magic.user.vo.AgentApplyVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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

    public List<AgentApplyVo> findByPage(Long ownerId, String account, Integer status, Integer page, Integer count) {
        try {
            int offset = (page - 1) * count;
            List<AgentApplyVo> list = agentApplyDao.find("findByPage", new String[]{"offset", "limit", "status", "account", "ownerId"}, new Object[]{offset, count, status, account, ownerId});
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public long getCount(Long ownerId, String account, Integer status) {
        try {
            return agentApplyDao.findCount("findCount", new String[]{"status", "account", "ownerId"}, new Object[]{status, account, ownerId});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }
}
