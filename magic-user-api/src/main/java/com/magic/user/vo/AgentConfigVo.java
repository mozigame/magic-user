package com.magic.user.vo;

/**
 * User: joey
 * Date: 2017/5/12
 * Time: 16:33
 */
public class AgentConfigVo {

    private Long agentId;   //代理id
    private Integer returnScheme;   //退佣方案编号
    private String returnSchemeName;    //退佣方案名称
    private Integer adminCost;  //行政成本编号
    private String adminCostName;   //行政成本名称
    private Integer feeScheme;  //手续费方案编号
    private String feeSchemeName;   //手续费方案名称

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Integer getReturnScheme() {
        return returnScheme;
    }

    public void setReturnScheme(Integer returnScheme) {
        this.returnScheme = returnScheme;
    }

    public String getReturnSchemeName() {
        return returnSchemeName;
    }

    public void setReturnSchemeName(String returnSchemeName) {
        this.returnSchemeName = returnSchemeName;
    }

    public Integer getAdminCost() {
        return adminCost;
    }

    public void setAdminCost(Integer adminCost) {
        this.adminCost = adminCost;
    }

    public String getAdminCostName() {
        return adminCostName;
    }

    public void setAdminCostName(String adminCostName) {
        this.adminCostName = adminCostName;
    }

    public Integer getFeeScheme() {
        return feeScheme;
    }

    public void setFeeScheme(Integer feeScheme) {
        this.feeScheme = feeScheme;
    }

    public String getFeeSchemeName() {
        return feeSchemeName;
    }

    public void setFeeSchemeName(String feeSchemeName) {
        this.feeSchemeName = feeSchemeName;
    }
}
