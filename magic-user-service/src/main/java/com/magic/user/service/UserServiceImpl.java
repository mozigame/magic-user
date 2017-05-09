package com.magic.user.service;

import com.magic.api.commons.model.Page;
import com.magic.user.dao.AgentDao;
import com.magic.user.entity.User;
import com.magic.user.storage.AgentDbService;
import com.magic.user.storage.StockDbService;
import com.magic.user.storage.UserDbService;
import com.magic.user.storage.UserMongoService;
import com.magic.user.util.UserVo;
import com.magic.user.vo.UserCondition;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/6
 * Time: 17:14
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource(name = "userDbService")
    private UserDbService userDbService;
    @Resource(name = "userMongoService")
    private UserMongoService userMongoService;
    @Resource(name = "stockDbService")
    private StockDbService stockDbService;
    @Resource(name = "agentDbService")
    private AgentDbService agentDbService;


    @Override
    public User get(long userId) {
        return stockDbService.get(userId);
    }

    @Override
    public List<Map<String, Object>> findAllStock() {
        return stockDbService.findAll();
    }

    @Override
    public Map<String, Object> getStockDetail(long id) {
        return stockDbService.getDetail(id);
    }

    @Override
    public int updatePwd(long id, String pwd) {
        return stockDbService.updatePwd(id, pwd);
    }

    @Override
    public int update(User user) {
        return stockDbService.update(user);
    }

    @Override
    public Long addStock(User user) {
        return (Long) stockDbService.insert("addAgent", null, user);
    }

    @Override
    public int disable(long id, int status) {
        return stockDbService.update("updateDisable", new String[]{"id", "status"}, new Object[]{id, status});
    }


    @Override
    public List<Map<String, Object>> findAgentByPage(UserCondition userCondition) {
        return agentDbService.findByPage(userCondition);
    }

    @Override
    public long getAgentCount(UserCondition userCondition) {
        return agentDbService.getCount(userCondition);
    }

    @Override
    public long addAgent(User user) {
        return (long) agentDbService.insert("addAgent", null, user);
    }

    @Override
    public long getOwnerIdByStock(long id) {
        return (long) userDbService.get("getOwnerIdByStock", null, id);
    }

    @Override
    public Map<String, Object> getAgentDetail(long id) {
        return (Map<String, Object>) userDbService.get("agentDetail", null, id);
    }
}
