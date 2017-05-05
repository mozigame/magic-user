package com.magic.user.entity;

/**
 * @Doc 代理参数配置
 */
public class AgentConfig {
    private int id;

    private long agentId;  //代理id

    private int returnSchemeId; //退佣方案id

    private int adminCostId;    //行政成本方案id

    private int feeId;  //手续费方案id

    private int discount;   //优惠扣除

    private int cost;   //返水成本

    private String domain;  //域名

    private String temp1;

    private String temp2;

    private String temp3;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public int getReturnSchemeId() {
        return returnSchemeId;
    }

    public void setReturnSchemeId(int returnSchemeId) {
        this.returnSchemeId = returnSchemeId;
    }

    public int getAdminCostId() {
        return adminCostId;
    }

    public void setAdminCostId(int adminCostId) {
        this.adminCostId = adminCostId;
    }

    public int getFeeId() {
        return feeId;
    }

    public void setFeeId(int feeId) {
        this.feeId = feeId;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
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