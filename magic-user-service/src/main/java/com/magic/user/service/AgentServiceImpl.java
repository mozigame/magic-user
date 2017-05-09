package com.magic.user.service;

import com.magic.user.dao.AgentDao;
import com.magic.user.entity.User;
import com.magic.user.storage.AgentDbService;
import com.magic.user.vo.UserCondition;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 20:15
 */
@Service("agentService")
public class AgentServiceImpl implements AgentService {

    @Resource
    private AgentDbService agentDbService;

    @Override
    public List<Map<String, Object>> findByPage(UserCondition userCondition) {
        return agentDbService.findByPage(userCondition);
    }

    @Override
    public long getCount(UserCondition userCondition) {
        return agentDbService.getCount(userCondition);
    }

    @Override
    public long add(User user) {
        return agentDbService.insert(user);
    }
}
