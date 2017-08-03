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
    private Long updateTime;    //修改时间
    private Integer status; //状态
    private Long level;  //层级
    private Integer depositCount;//存款次数 1
    private Integer withdrawCount;//取款次数 1
    private Long depositMoney;//充值总额 1
    private Long withdrawMoney;   //提款总额 1
    private Integer currencyType;   //币种
    private Long lastDepositMoney;   //最近存款
    private Long lastWithdrawMoney;  //最近提款
    private Long maxDepositMoney;   //最大存款额 1
    private Long maxWithdrawMoney;   //最大取款额

    /** 电话 */
    private String telephone;

    public static final String telephoneString = "telephone";


    /** 银行卡号 */
    private String bankCardNo;

    public static final String bankCardNoString = "bankCardNo";


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

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Long getLastDepositMoney() {
        return lastDepositMoney;
    }

    public void setLastDepositMoney(Long lastDepositMoney) {
        this.lastDepositMoney = lastDepositMoney;
    }

    public Long getLastWithdrawMoney() {
        return lastWithdrawMoney;
    }

    public void setLastWithdrawMoney(Long lastWithdrawMoney) {
        this.lastWithdrawMoney = lastWithdrawMoney;
    }

    public Long getMaxDepositMoney() {
        return maxDepositMoney;
    }

    public void setMaxDepositMoney(Long maxDepositMoney) {
        this.maxDepositMoney = maxDepositMoney;
    }

    public Long getMaxWithdrawMoney() {
        return maxWithdrawMoney;
    }

    public void setMaxWithdrawMoney(Long maxWithdrawMoney) {
        this.maxWithdrawMoney = maxWithdrawMoney;
    }

    /**
     * getter for bankCardNo
     */

    public String getBankCardNo() {
        return bankCardNo;
    }

    /**
     * setter for bankCardNo
     */
    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }


    /**
     * getter for telephone
     */

    public String getTelephone() {
        return telephone;
    }

    /**
     * setter for telephone
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
