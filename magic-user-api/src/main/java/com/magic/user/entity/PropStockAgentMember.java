package com.magic.user.entity;

/**
 * @Doc  业主股东代理会员数量关联表
 */
public class PropStockAgentMember {
    private long id;

    private long proprietorId;    //业主id

    private long stockId;   //股东id

    private long agentId;   //代理id

    private int memNumber;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProprietorId() {
        return proprietorId;
    }

    public void setProprietorId(long proprietorId) {
        this.proprietorId = proprietorId;
    }

    public long getStockId() {
        return stockId;
    }

    public void setStockId(long stockId) {
        this.stockId = stockId;
    }

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public int getMemNumber() {
        return memNumber;
    }

    public void setMemNumber(int memNumber) {
        this.memNumber = memNumber;
    }
}