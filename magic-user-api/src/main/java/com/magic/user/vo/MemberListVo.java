package com.magic.user.vo;

/**
 * MemberListVo
 *
 * @author zj
 * @date 2017/5/8
 */
public class MemberListVo {

    /**
     * 会员ID
     */
    private long id;
    /**
     * 会员帐号
     */
    private String account;
    /**
     * 所属代理ID
     */
    private long agentId;
    /**
     * 所属代理
     */
    private String agent;
    /**
     * 会员层级
     */
    private String level;
    /**
     * 余额
     */
    private String balance;
    /**
     * 返水方案编号
     */
    private int returnWater;
    /**
     * 当前返水方案
     */
    private String returnWaterName;
    /**
     * 注册时间
     */
    private String registerTime;
    /**
     * 最近登录时间
     */
    private String lastLoginTime;
    /**
     * 状态
     */
    private int status;
    /**
     * 状态描述
     */
    private String showStatus;

    /**
     * 存款次数
     */
    private Integer depositCount;

    /**
     * 存款总额
     */
    private Long depositMoney;

    /**
     * 最大存款数额
     */
    private Long maxDepositMoney;

    /**
     * 取款次数
     */
    private Integer withdrawCount;

    /**
     * 取款总额
     */
    private Long withdrawMoney;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public int getReturnWater() {
        return returnWater;
    }

    public void setReturnWater(int returnWater) {
        this.returnWater = returnWater;
    }

    public String getReturnWaterName() {
        return returnWaterName;
    }

    public void setReturnWaterName(String returnWaterName) {
        this.returnWaterName = returnWaterName;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(String showStatus) {
        this.showStatus = showStatus;
    }


    public Integer getDepositCount() {
        return depositCount;
    }

    public void setDepositCount(Integer depositCount) {
        this.depositCount = depositCount;
    }

    public Long getDepositMoney() {
        return depositMoney;
    }

    public void setDepositMoney(Long depositMoney) {
        this.depositMoney = depositMoney;
    }

    public Long getMaxDepositMoney() {
        return maxDepositMoney;
    }

    public void setMaxDepositMoney(Long maxDepositMoney) {
        this.maxDepositMoney = maxDepositMoney;
    }

    public Integer getWithdrawCount() {
        return withdrawCount;
    }

    public void setWithdrawCount(Integer withdrawCount) {
        this.withdrawCount = withdrawCount;
    }

    public Long getWithdrawMoney() {
        return withdrawMoney;
    }

    public void setWithdrawMoney(Long withdrawMoney) {
        this.withdrawMoney = withdrawMoney;
    }
}
