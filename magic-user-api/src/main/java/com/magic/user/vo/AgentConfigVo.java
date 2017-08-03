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
    private Integer discount;   //优惠扣除
    private Integer cost;   //返水成本
    private String userLevelName;//层级名称

    private Long registerOfferId;//会员注册优惠方案ID
    private String registerOfferName;//会员注册优惠方案名称

    public String getUserLevelName() {
        return userLevelName;
    }

    public void setUserLevelName(String userLevelName) {
        this.userLevelName = userLevelName;
    }

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

    public Long getRegisterOfferId() {
        return registerOfferId;
    }

    public void setRegisterOfferId(Long registerOfferId) {
        this.registerOfferId = registerOfferId;
    }

    public String getRegisterOfferName() {
        return registerOfferName;
    }

    public void setRegisterOfferName(String registerOfferName) {
        this.registerOfferName = registerOfferName;
    }
}
