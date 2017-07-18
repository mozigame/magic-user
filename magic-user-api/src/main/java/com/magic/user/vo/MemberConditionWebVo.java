package com.magic.user.vo;

/**
 * morton 2017-07-15
 */
public class MemberConditionWebVo {

    private String id;    //
    private Long memberId;    //会员id
    private String memberName;    //会员名称
    private Long agentId;   //代理id
    private String agentName;   //代理名称
    private Long stockId;   //股东id
    private Long ownerId;   //业主id
    private Long registerTime;  //注册时间
    private Long updateTime;    //修改时间
    private Integer status; //状态
    private Long level;  //层级
    private Integer depositCount;//存款次数
    private Integer withdrawCount;//取款次数
    private String depositMoney;//充值总额
    private String withdrawMoney;   //提款总额
    private Integer currencyType;   //币种
    private String lastDepositMoney;   //最近存款
    private String lastWithdrawMoney;  //最近提款
    private String maxDepositMoney;   //最大存款额
    private String maxWithdrawMoney;   //最大取款额

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
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

    public Long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Long registerTime) {
        this.registerTime = registerTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public Integer getDepositCount() {
        return depositCount;
    }

    public void setDepositCount(Integer depositCount) {
        this.depositCount = depositCount;
    }

    public Integer getWithdrawCount() {
        return withdrawCount;
    }

    public void setWithdrawCount(Integer withdrawCount) {
        this.withdrawCount = withdrawCount;
    }

    public String getDepositMoney() {
        return depositMoney;
    }

    public void setDepositMoney(String depositMoney) {
        this.depositMoney = depositMoney;
    }

    public String getWithdrawMoney() {
        return withdrawMoney;
    }

    public void setWithdrawMoney(String withdrawMoney) {
        this.withdrawMoney = withdrawMoney;
    }

    public Integer getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(Integer currencyType) {
        this.currencyType = currencyType;
    }

    public String getLastDepositMoney() {
        return lastDepositMoney;
    }

    public void setLastDepositMoney(String lastDepositMoney) {
        this.lastDepositMoney = lastDepositMoney;
    }

    public String getLastWithdrawMoney() {
        return lastWithdrawMoney;
    }

    public void setLastWithdrawMoney(String lastWithdrawMoney) {
        this.lastWithdrawMoney = lastWithdrawMoney;
    }

    public String getMaxDepositMoney() {
        return maxDepositMoney;
    }

    public void setMaxDepositMoney(String maxDepositMoney) {
        this.maxDepositMoney = maxDepositMoney;
    }

    public String getMaxWithdrawMoney() {
        return maxWithdrawMoney;
    }

    public void setMaxWithdrawMoney(String maxWithdrawMoney) {
        this.maxWithdrawMoney = maxWithdrawMoney;
    }
}
