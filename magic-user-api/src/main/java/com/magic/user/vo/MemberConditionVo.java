package com.magic.user.vo;

/**
 * User: joey
 * Date: 2017/5/17
 * Time: 12:03
 *
 * @Doc 会员mongo对象
 */
public class MemberConditionVo {

    private String id;    //
    private Long memberId;    //会员id
    private String memberName;    //会员名称
    private Long agentId;   //代理id
    private String agentName;   //代理名称
    private Long stockId;   //股东id
    private Long ownerId;   //业主id
    private Long registerTime;  //注册时间
    private Integer status; //状态
    private Integer level;  //层级
    private Integer depositCount;//存款次数
    private Integer withdrawCount;//取款次数
    private Long depositMoney;//充值总额
    private Long withdrawMoney;   //提款总额
    private Integer currencyType;   //币种

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
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

    public Integer getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(Integer currencyType) {
        this.currencyType = currencyType;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
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
}