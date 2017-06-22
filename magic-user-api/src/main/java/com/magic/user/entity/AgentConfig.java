package com.magic.user.entity;

/**
 * @Doc 代理参数配置
 */
public class AgentConfig {
    private Integer id;

    private Long ownerId;   //业主id

    private Long agentId;  //代理id

    private Integer returnSchemeId; //退佣方案id

    private Integer adminCostId;    //行政成本方案id

    private Integer feeId;  //手续费方案id

    private Integer discount;   //优惠扣除

    private Integer cost;   //返水成本

    private String domain;  //域名

    private String temp1;

    private String temp2;

    private String temp3;

    public AgentConfig() {
    }

    public AgentConfig(Long agentId, Integer returnSchemeId, Integer adminCostId, Integer feeId) {
        this.agentId = agentId;
        this.returnSchemeId = returnSchemeId;
        this.adminCostId = adminCostId;
        this.feeId = feeId;
    }

    public AgentConfig(Long agentId, Integer returnSchemeId, Integer adminCostId, Integer feeId, String domain) {
        this.agentId = agentId;
        this.returnSchemeId = returnSchemeId;
        this.adminCostId = adminCostId;
        this.feeId = feeId;
        this.domain = domain;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Integer getReturnSchemeId() {
        return returnSchemeId;
    }

    public void setReturnSchemeId(Integer returnSchemeId) {
        this.returnSchemeId = returnSchemeId;
    }

    public Integer getAdminCostId() {
        return adminCostId;
    }

    public void setAdminCostId(Integer adminCostId) {
        this.adminCostId = adminCostId;
    }

    public Integer getFeeId() {
        return feeId;
    }

    public void setFeeId(Integer feeId) {
        this.feeId = feeId;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getTemp1() {
        return temp1;
    }

    public void setTemp1(String temp1) {
        this.temp1 = temp1;
    }

    public String getTemp2() {
        return temp2;
    }

    public void setTemp2(String temp2) {
        this.temp2 = temp2;
    }

    public String getTemp3() {
        return temp3;
    }

    public void setTemp3(String temp3) {
        this.temp3 = temp3;
    }
}