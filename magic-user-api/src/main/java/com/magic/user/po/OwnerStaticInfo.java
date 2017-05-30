package com.magic.user.po;

import java.io.Serializable;

/**
 * OwnerStaticInfo
 *
 * @author zj
 * @date 2017/5/29
 */
public class OwnerStaticInfo implements Serializable{

    /**
     * 业主ID
     */
    private Long ownerId;
    /**
     * 股东数
     */
    private Integer stocks;
    /**
     * 代理数
     */
    private Integer agents;
    /**
     * 会员数
     */
    private Integer members;
    /**
     *
     */
    private Integer workers;

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getStocks() {
        return stocks;
    }

    public void setStocks(Integer stocks) {
        this.stocks = stocks;
    }

    public Integer getAgents() {
        return agents;
    }

    public void setAgents(Integer agents) {
        this.agents = agents;
    }

    public Integer getMembers() {
        return members;
    }

    public void setMembers(Integer members) {
        this.members = members;
    }

    public Integer getWorkers() {
        return workers;
    }

    public void setWorkers(Integer workers) {
        this.workers = workers;
    }
}
