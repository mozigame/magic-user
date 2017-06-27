package com.magic.user.service;

import com.magic.api.commons.model.PageBean;
import com.magic.user.entity.Login;
import com.magic.user.entity.User;
import com.magic.user.vo.AgentInfoVo;
import com.magic.user.vo.StockInfoVo;

import java.util.List;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:21
 */
public interface UserService {

    /**
     * 根据用户id获取用户信息
     *
     * @param id
     * @return
     */
    User get(Long id);

    /**
     * @return
     * @Doc 获取所有股东
     */
    List<StockInfoVo> findAllStock(Long ownerId);

    /**
     * @param id
     * @return
     * @Doc 获取股东详情
     */
    StockInfoVo getStockDetail(Long id);

    /**
     * @param ids
     * @return
     * @Doc 根据多个id获取代理信息
     */
    List<AgentInfoVo> findAgents(List<Long> ids);

    /**
     * 添加代理
     *
     * @param user
     * @return
     */
    boolean addAgent(User user);

    /**
     * 添加子账号
     * @param user
     * @return
     */
    boolean addWorker(User user);

    /**
     * @param id
     * @return
     * @Doc 根据股东id获取业主id
     */
    long getOwnerIdByStock(Long id);

    /**
     * 根据推广码查询代理数据
     *
     * @param proCode
     * @return
     */
    User getUserByCode(String proCode);

    /**
     * @param id
     * @return
     * @Doc 查询代理详情
     */
    AgentInfoVo getAgentDetail(Long id);

    /**
     * 获取用户数据
     *
     * @param uid
     * @return
     */
    User getUserById(Long uid);

    /**
     * @param user
     * @return
     * @Doc 修改用户信息
     */
    boolean update(User user);

    /**
     * 修改子账号
     * @param user
     * @return
     */
    boolean updateWorker(User user);

    /**
     * @param user
     * @return
     * @Doc 添加股东
     */
    boolean addStock(User user);

    /**
     * @param user
     * @return
     * @Doc 启用停用账号
     */
    boolean disable(User user);

    /**
     * 查询子账号列表，子账号在业主平台只包括工作人员
     * @param ownerId
     * @param account
     * @param realname
     * @param page
     * @param count
     * @return
     */
    List<User> findWorkers(Long ownerId, String account, String realname, Integer roleId, Integer page, Integer count);

    /**
     * 获取子账号的数量
     * @param ownerId
     * @param account
     * @param realname
     * @return
     */
    Long getWorkerCount(Long ownerId, String account, String realname, Integer roleId);

    /**
     * 获取一定周期内的代理列表
     * @param startTime
     * @param endTime
     * @param ownerId
     * @return
     */
    List<User> periodAgentList(Long startTime, Long endTime, Long ownerId);
}
