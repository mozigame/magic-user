package com.magic.user.vo;

/**
 * MemberFundInfo
 *
 * @author zj
 * @date 2017/5/8
 */
public class AgentFundInfo {

    private Integer members;//会员数量
    private Integer depositMembers;//存款会员数量
    private String depositTotalMoney;//存款金额
    private String withdrawTotalMoney;//取款金额
    private String betTotalMoney;//总投注额
    private String betEffMoney;//总有效投注额
    private String gains;//损益

    public Integer getMembers() {
        return members;
    }

    public void setMembers(Integer members) {
        this.members = members;
    }

    public Integer getDepositMembers() {
        return depositMembers;
    }

    public void setDepositMembers(Integer depositMembers) {
        this.depositMembers = depositMembers;
    }

    public String getDepositTotalMoney() {
        return depositTotalMoney;
    }

    public void setDepositTotalMoney(String depositTotalMoney) {
        this.depositTotalMoney = depositTotalMoney;
    }

    public String getWithdrawTotalMoney() {
        return withdrawTotalMoney;
    }

    public void setWithdrawTotalMoney(String withdrawTotalMoney) {
        this.withdrawTotalMoney = withdrawTotalMoney;
    }

    public String getBetTotalMoney() {
        return betTotalMoney;
    }

    public void setBetTotalMoney(String betTotalMoney) {
        this.betTotalMoney = betTotalMoney;
    }

    public String getBetEffMoney() {
        return betEffMoney;
    }

    public void setBetEffMoney(String betEffMoney) {
        this.betEffMoney = betEffMoney;
    }

    public String getGains() {
        return gains;
    }

    public void setGains(String gains) {
        this.gains = gains;
    }
}
