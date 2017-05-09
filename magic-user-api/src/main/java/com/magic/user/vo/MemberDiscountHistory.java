package com.magic.user.vo;

/**
 * MemberDiscountHistory
 *
 * @author zj
 * @date 2017/5/8
 */
public class MemberDiscountHistory {

    private String totalMoney;//获得优惠总额
    private Integer numbers;//获得优惠次数
    private String returnWaterTotalMoney;//返水总额

    public String getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(String totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Integer getNumbers() {
        return numbers;
    }

    public void setNumbers(Integer numbers) {
        this.numbers = numbers;
    }

    public String getReturnWaterTotalMoney() {
        return returnWaterTotalMoney;
    }

    public void setReturnWaterTotalMoney(String returnWaterTotalMoney) {
        this.returnWaterTotalMoney = returnWaterTotalMoney;
    }
}
