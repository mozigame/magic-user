package com.magic.user.vo;

/**
 * MemberFundInfo
 *
 * @author zj
 * @date 2017/5/8
 */
public class MemberFundInfo {

    private String balance;//账户余额
    private Integer depositNumbers;//存款总次数
    private String depositTotalMoney;//存款总金额
    private String lastDeposit;//最近存款
    private Integer withdrawNumbers;//取款总次数
    private String withdrawTotalMoney;//取款金额
    private String lastWithdraw;//最近取款

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public Integer getDepositNumbers() {
        return depositNumbers;
    }

    public void setDepositNumbers(Integer depositNumbers) {
        this.depositNumbers = depositNumbers;
    }

    public String getDepositTotalMoney() {
        return depositTotalMoney;
    }

    public void setDepositTotalMoney(String depositTotalMoney) {
        this.depositTotalMoney = depositTotalMoney;
    }

    public String getLastDeposit() {
        return lastDeposit;
    }

    public void setLastDeposit(String lastDeposit) {
        this.lastDeposit = lastDeposit;
    }

    public Integer getWithdrawNumbers() {
        return withdrawNumbers;
    }

    public void setWithdrawNumbers(Integer withdrawNumbers) {
        this.withdrawNumbers = withdrawNumbers;
    }

    public String getWithdrawTotalMoney() {
        return withdrawTotalMoney;
    }

    public void setWithdrawTotalMoney(String withdrawTotalMoney) {
        this.withdrawTotalMoney = withdrawTotalMoney;
    }

    public String getLastWithdraw() {
        return lastWithdraw;
    }

    public void setLastWithdraw(String lastWithdraw) {
        this.lastWithdraw = lastWithdraw;
    }
}
