package com.magic.user.entity;

/**
 * @Doc 业主股东代理会员数量关联表
 */
public class OwnerStockAgentMember {
    private long id;

    private long ownerId;    //业主id

    private long stockId;   //股东id

    private long agentId;   //代理id

    private int memNumber;

    public OwnerStockAgentMember() {

    }

    public OwnerStockAgentMember(long ownerId, long stockId, long agentId) {
        this.ownerId = ownerId;
        this.stockId = stockId;
        this.agentId = agentId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
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