package com.magic.user.vo;

/**
 * MemberPreferScheme
 *
 * @author zj
 * @date 2017/5/8
 */
public class MemberPreferScheme {

    private int level;//所属层级
    private String showLevel;//层面描述
    private String onlineDiscount;//线上入款优惠方案
    private String depositFee;//入款手续费
    private String withdrawFee;//出款手续费
    private String returnWater;//当前返水方案
    private String depositDiscountScheme;//公司入款优惠方案

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getShowLevel() {
        return showLevel;
    }

    public void setShowLevel(String showLevel) {
        this.showLevel = showLevel;
    }

    public String getOnlineDiscount() {
        return onlineDiscount;
    }

    public void setOnlineDiscount(String onlineDiscount) {
        this.onlineDiscount = onlineDiscount;
    }

    public String getDepositFee() {
        return depositFee;
    }

    public void setDepositFee(String depositFee) {
        this.depositFee = depositFee;
    }

    public String getWithdrawFee() {
        return withdrawFee;
    }

    public void setWithdrawFee(String withdrawFee) {
        this.withdrawFee = withdrawFee;
    }

    public String getReturnWater() {
        return returnWater;
    }

    public void setReturnWater(String returnWater) {
        this.returnWater = returnWater;
    }

    public String getDepositDiscountScheme() {
        return depositDiscountScheme;
    }

    public void setDepositDiscountScheme(String depositDiscountScheme) {
        this.depositDiscountScheme = depositDiscountScheme;
    }
}
