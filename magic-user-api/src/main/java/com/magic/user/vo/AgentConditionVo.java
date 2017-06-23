package com.magic.user.vo;

/**
 * User: joey
 * Date: 2017/5/17
 * Time: 12:26
 * @Doc 代理mongo对象
 */
public class AgentConditionVo {

    private String id;
    private Long agentId;   //代理id
    private String agentName;   //代理名称
    private Long stockId;   //股东id
    private Long ownerId;   //业主id
    private Long registerTime;  //注册时间
    private Long updateTime;    //修改时间
    private String generalizeCode;  //推广码
    private Integer members;    //会员数
    private Long depositMoney;//所有会员存款总额
    private Long withdrawMoney;   //所有会员取款总额
    private Integer status; //状态

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public Long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Long registerTime) {
        this.registerTime = registerTime;
    }

    public String getGeneralizeCode() {
        return generalizeCode;
    }

    public void setGeneralizeCode(String generalizeCode) {
        this.generalizeCode = generalizeCode;
    }

    public Integer getMembers() {
        return members;
    }

    public void setMembers(Integer members) {
        this.members = members;
    }

    public Long getDepositMoney() {
        return depositMoney;
    }

    public void setDepositMoney(Long depositMoney) {
        this.depositMoney = depositMoney;
    }

    public Long getWithdrawMoney() {
        return withdrawMoney;
    }

    public void setWithdrawMoney(Long withdrawMoney) {
        this.withdrawMoney = withdrawMoney;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}
