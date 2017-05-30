package com.magic.user.storage;

/**
 * CountRedisStorageServiceImpl
 *
 * @author zj
 * @date 2017/5/29
 */
public interface CountRedisStorageService {
    /**
     * 业主下股东数+1
     *
     * @param ownerId
     * @return
     */
    boolean incrStock(Long ownerId);

    /**
     * 业主下代理数+1
     *
     * @param ownerId
     * @return
     */
    boolean incrAgent(Long ownerId);

    /**
     * 业主下子账号数+1
     *
     * @param ownerId
     * @return
     */
    boolean incrWorker(Long ownerId);

    /**
     * 业主下会员数+1
     *
     * @param ownerId
     * @return
     */
    boolean incrMember(Long ownerId);

    /**
     * 获取业主下股东数量
     *
     * @param ownerId
     * @return
     */
    int getStocks(Long ownerId);

    /**
     * 获取业主下代理数量
     *
     * @param ownerId
     * @return
     */
    int getAgents(Long ownerId);

    /**
     * 获取业主下会员数量
     *
     * @param ownerId
     * @return
     */
    int getMembers(Long ownerId);

    /**
     * 获取业主下子账号数
     *
     * @param ownerId
     * @return
     */
    int getWorkers(Long ownerId);
}
