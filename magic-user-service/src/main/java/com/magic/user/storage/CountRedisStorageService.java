package com.magic.user.storage;

import com.magic.user.entity.Member;

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

    /**
     * 原子性 +1
     *
     * @param key
     * @return
     */
    long incr(String key);

    /**
     * 原子性 -1
     *
     * @param key
     * @return
     */
    long decr(String key);

    /**
     * 按天记录登陆次数
     *
     * @param member
     * @param date
     * @return
     */
    boolean countLogins(Member member, String date);

    /**
     * 获取活跃用户数
     *
     * @param key
     * @param uid
     * @return
     */
    long getCount(String key, Long uid);

    /**
     * 获取活跃用户数，所有业主下的，指定某天
     *
     * @param key
     * @return
     */
    long getCounts(String key);

    /**
     * 检查1天内超过10次注册的ip
     * @param ip
     * @return
     */
    void addRegisterIpCount(String ip);

    long getRegisterIpCount(String ip);
}
