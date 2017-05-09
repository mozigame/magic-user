package com.magic.user.service;

import com.magic.api.commons.model.Page;
import com.magic.user.entity.AgentApply;
import com.magic.user.storage.AgentApplyDbService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/9
 * Time: 19:38
 */
@Service("agentApplyService")
public class AgentApplyServiceImpl implements AgentApplyService {

    @Resource(name = "agentApplyDbService")
    private AgentApplyDbService agentApplyDbService;

    @Override
    public long add(AgentApply agentApply) {
        return agentApplyDbService.insert(agentApply);
    }

    @Override
    public Page<Map<String, Object>> findByPage(AgentApply agentApply, Page page) {
        return agentApplyDbService.findByPage(agentApply, page);
    }

    @Override
    public Map<String, Object> agentReviewInfo(long applyId) {
        return (Map<String, Object>) agentApplyDbService.get(applyId);
    }

    @Override
    public int updateStatus(long applyId, int status) {
        return agentApplyDbService.update("updateStatus", new String[]{"status", "id"}, new Object[]{status, applyId});
    }

    @Override
    public AgentApply get(long id) {
        return agentApplyDbService.get(id);
    }
}
