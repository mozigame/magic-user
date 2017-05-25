package com.magic.user.service;

import com.magic.api.commons.model.Page;
import com.magic.user.entity.AgentApply;
import com.magic.user.storage.AgentApplyDbService;
import com.magic.user.vo.AgentApplyVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
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
        Long result = agentApplyDbService.insert(agentApply);
        return result == null ? 0L : result;
    }

    @Override
    public List<AgentApplyVo> findByPage(Long ownerId, String account, Integer status, Integer page, Integer count) {
        return agentApplyDbService.findByPage(ownerId, account, status, page, count);
    }

    @Override
    public long getCount(Long ownerId, String account, Integer status) {
        return agentApplyDbService.getCount(ownerId, account, status);
    }

    @Override
    public AgentApplyVo agentReviewInfo(Long applyId) {
        return (AgentApplyVo) agentApplyDbService.get("getDetail", new String[]{"id"}, applyId);
    }

    @Override
    public int updateStatus(Long applyId, Integer status) {
        return agentApplyDbService.update("updateStatus", new String[]{"status", "id"}, new Object[]{status, applyId});
    }

    @Override
    public AgentApply get(Long id) {
        return agentApplyDbService.get(id);
    }
}
