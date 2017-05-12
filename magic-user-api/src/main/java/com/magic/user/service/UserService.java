package com.magic.user.service;

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
    List<StockInfoVo> findAllStock();

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
    long addAgent(User user);

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
     * 用户更新
     *
     * @param id
     * @param realname
     * @param telephone
     * @param email
     * @param bankCardNo
     * @param bank
     * @param bankDeposit
     * @return
     */
    boolean updateUser(Long id, String realname, String telephone, String email, String bankCardNo, String bank, String bankDeposit);


    /**
     * @param user
     * @return
     * @Doc 修改用户信息
     */
    int update(User user);

    /**
     * @param user
     * @return
     * @Doc 添加股东
     */
    long addStock(User user);

    /**
     * @param id
     * @param status
     * @return
     * @Doc 启用停用账号
     */
    int disable(Long id, Integer status);
}
