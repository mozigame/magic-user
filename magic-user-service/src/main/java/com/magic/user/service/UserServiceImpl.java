package com.magic.user.service;

import com.magic.user.entity.User;
import com.magic.user.storage.AgentDbService;
import com.magic.user.storage.StockDbService;
import com.magic.user.storage.UserDbService;
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
    public int update(User user) {
        return stockDbService.update(user);
    }

    @Override
    public long addStock(User user) {
        return stockDbService.insert(user);
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
        return agentDbService.insert(user);
    }

    @Override
    public long getOwnerIdByStock(long id) {
        return (long) userDbService.get("getOwnerIdByStock", null, id);
    }

    @Override
    public User getUserByCode(String proCode) {
        List<User> list = userDbService.find("findAgentByProCode", null, proCode);
        return list == null ? null : list.get(0);
    }

    @Override
    public Map<String, Object> getAgentDetail(long id) {
        return (Map<String, Object>) userDbService.get("agentDetail", null, id);
    }

    @Override
    public User getUserById(long uid) {
        return userDbService.get(uid);
    }

    @Override
    public boolean updateUser(long id, String realname, String telephone, String email, String bankCardNo, String bank, String bankDeposit) {
        return false;
    }
}
