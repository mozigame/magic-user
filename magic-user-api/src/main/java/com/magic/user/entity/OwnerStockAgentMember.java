package com.magic.user.entity;

/**
 * @Doc 业主股东代理会员数量关联表
 */
public class OwnerStockAgentMember {
    private Long id;

    private Long ownerId;    //业主id

    private Long stockId;   //股东id

    private Long agentId;   //代理id

    private Integer memNumber;

    public OwnerStockAgentMember() {

    }

    public OwnerStockAgentMember(Long ownerId, Long stockId, Long agentId) {
        this.ownerId = ownerId;
        this.stockId = stockId;
        this.agentId = agentId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Integer getMemNumber() {
        return memNumber;
    }

    public void setMemNumber(Integer memNumber) {
        this.memNumber = memNumber;
    }
}