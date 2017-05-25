package com.magic.user.service;

import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.model.PageBean;
import com.magic.user.entity.Login;
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
            user = userDbService.get(userId);
            if (user != null) {
                if (!userRedisStorageService.addUser(user)) {
                    ApiLogger.warn("add user info to redis error,userId:" + user.getUserId());
                }
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
        return !(result == null || result <= 0);
    }

    @Override
    public boolean disable(User user) {
        int result = stockDbService.update("updateDisable", new String[]{"id", "status"}, new Object[]{user.getUserId(), user.getStatus().value()});
        if (result > 0) {
            if (!userRedisStorageService.updateUser(user)) {
                ApiLogger.warn("update user status to redis error,userId:" + user.getUserId());
            }
        }
        return result > 0;
    }

    @Override
    public List<User> findWorkers(Long ownerId, String account, String realname, Integer page, Integer count) {
        Integer offset = page == null ? null : (page - 1) * count;
        return agentDbService.find("findWorkers", new String[]{"ownerId", "account", "realname", "offset", "limit"}, new Object[]{ownerId, account, realname, offset, count});
    }

    @Override
    public Long getWorkerCount(Long ownerId, String account, String realname) {
        Long count = (Long) agentDbService.get("getWorkerCount", new String[]{"ownerId", "account", "realname"}, new Object[]{ownerId, account, realname});
        return count == null ? 0 : count;
    }


    @Override
    public List<AgentInfoVo> findAgents(List<Long> ids) {
        return agentDbService.findCustom("findAgentByIds", new String[]{"ids"}, ids);
    }

    @Override
    public boolean addAgent(User user) {
        Long result = agentDbService.insert(user);
        return !(result == null || result <= 0);
    }

    @Override
    public boolean addWorker(User user) {
        Long result = agentDbService.insert(user);
        return result != null && result > 0;
    }

    @Override
    public long getOwnerIdByStock(Long id) {
        Long result = (Long) userDbService.get("getOwnerIdByStock", new String[]{"userId"}, id);
        return result == null ? 0 : result;
    }

    @Override
    public User getUserByCode(String proCode) {
        List<User> list = userDbService.find("findAgentByProCode", new String[]{"proCode"}, proCode);
        return (list != null && list.size() > 0) ? list.get(0) : null;
    }

    @Override
    public AgentInfoVo getAgentDetail(Long id) {
        return (AgentInfoVo) userDbService.get("agentDetail", new String[]{"userId"}, id);
    }

    @Override
    public User getUserById(Long uid) {
        return get(uid);
    }
}
