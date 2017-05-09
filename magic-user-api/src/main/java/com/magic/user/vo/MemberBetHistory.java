package com.magic.user.vo;

/**
 * MemberBetHistory
 *
 * @author zj
 * @date 2017/5/8
 */
public class MemberBetHistory {

    private String totalMoney;//累计投注额
    private String effMoney;//累计有效投注额
    private String gains;//盈亏金额

    public String getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(String totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getEffMoney() {
        return effMoney;
    }

    public void setEffMoney(String effMoney) {
        this.effMoney = effMoney;
    }

    public String getGains() {
        return gains;
    }

    public void setGains(String gains) {
        this.gains = gains;
    }
}
