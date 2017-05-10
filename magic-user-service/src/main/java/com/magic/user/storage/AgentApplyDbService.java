package com.magic.user.storage;

import com.magic.api.commons.atlas.core.BaseDao;
import com.magic.api.commons.model.Page;
import com.magic.user.dao.AgentApplyDao;
import com.magic.user.entity.AgentApply;
import com.magic.user.entity.User;
import com.magic.user.storage.base.BaseDbServiceImpl;
import com.magic.user.vo.UserCondition;
import org.apache.ibatis.session.RowBounds;
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

    public Page<Map<String, Object>> findByPage(AgentApply agentApply, Page page) {
        try {
            List<Map<String, Object>> list = agentApplyDao.find("findByPage", new String[]{"offset", "limit", "status", "account"}, new Object[]{page.getPageNo(), page.getPageSize(), agentApply.getStatus().value(), agentApply.getUsername()});
            long count = agentApplyDao.findCount("findCount", new String[]{"status", "account"}, new Object[]{agentApply.getStatus().value(), agentApply.getUsername()});
            page.setResult(list);
            page.setTotalCount(count);
            return page;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
