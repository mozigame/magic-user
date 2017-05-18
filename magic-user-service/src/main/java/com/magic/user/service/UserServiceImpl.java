package com.magic.user.service;

import com.magic.api.commons.ApiLogger;
import com.magic.user.entity.User;
import com.magic.user.storage.AgentDbService;
import com.magic.user.storage.StockDbService;
import com.magic.user.storage.UserDbService;
import com.magic.user.storage.UserRedisStorageService;
import com.magic.user.vo.AgentInfoVo;
import com.magic.user.vo.StockInfoVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * User: joey
 * Date: 2017/5/6
 * Time: 17:14
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource(name = "userDbService")
    private UserDbService userDbService;
    @Resource(name = "userRedisStorageService")
    private UserRedisStorageService userRedisStorageService;

    @Resource(name = "stockDbService")
    private StockDbService stockDbService;
    @Resource(name = "agentDbService")
    private AgentDbService agentDbService;


    @Override
    public User get(Long userId) {
        User user = userRedisStorageService.getUser(userId);
        if (user == null) {
            user = stockDbService.get(userId);
            if (user != null)
                if (!userRedisStorageService.addUser(user)) {
                    ApiLogger.warn("add user info to redis error,userId:" + user.getUserId());
                }
        }
        return user;
    }

    @Override
    public List<StockInfoVo> findAllStock(Long ownerId) {
        return stockDbService.findAll(ownerId);
    }

    @Override
    public StockInfoVo getStockDetail(Long id) {
        return stockDbService.getDetail(id);
    }

    @Override
    public boolean update(User user) {
        boolean result = stockDbService.update(user) > 0;
        if (result) {
            if (!userRedisStorageService.updateUser(user)) {
                ApiLogger.warn("update user info to redis error,userId:" + user.getUserId());
            }
        }
        return result;
    }

    @Override
    public boolean addStock(User user) {
        Long result = stockDbService.insert(user);
        return (result == null || result <= 0) ? false : true;
    }

    @Override
    public boolean disable(Long id, Integer status) {
        int result = stockDbService.update("updateDisable", new String[]{"id", "status"}, new Object[]{id, status});
        return result > 0;
    }


    @Override
    public List<AgentInfoVo> findAgents(List<Long> ids) {
        return agentDbService.findCustom("findAgentByIds", new String[]{"ids"}, ids);
    }

    @Override
    public boolean addAgent(User user) {
        Long result = agentDbService.insert(user);
        return (result == null || result <= 0) ? false : true;
    }

    @Override
    public long getOwnerIdByStock(Long id) {
        Long result = (Long) userDbService.get("getOwnerIdByStock", null, id);
        return result == null ? 0 : result;
    }

    @Override
    public User getUserByCode(String proCode) {
        List<User> list = userDbService.find("findAgentByProCode", null, proCode);
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }

    @Override
    public AgentInfoVo getAgentDetail(Long id) {
        return (AgentInfoVo) userDbService.get("agentDetail", null, id);
    }

    @Override
    public User getUserById(Long uid) {
        return userDbService.get(uid);
    }
}
