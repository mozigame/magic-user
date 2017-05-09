package com.magic.user.vo;

/**
 * LevelCondition
 *
 * @author zj
 * @date 2017/5/8
 */
public class LevelCondition {

    private Integer depositNumbers;//存款次数
    private String depositTotalMoney;//存款总额
    private String maxDepositMoney;//最大存款数额
    private Integer withdrawNumbers;//取款次数
    private String withdrawTotalMoney;//取款总额

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

    public String getMaxDepositMoney() {
        return maxDepositMoney;
    }

    public void setMaxDepositMoney(String maxDepositMoney) {
        this.maxDepositMoney = maxDepositMoney;
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
}
