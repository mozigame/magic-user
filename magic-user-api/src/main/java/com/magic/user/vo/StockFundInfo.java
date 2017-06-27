package com.magic.user.vo;

/**
 * MemberFundInfo
 *
 * @author zj
 * @date 2017/5/8
 */
public class StockFundInfo {

    private Integer bets;//投注会员数量
    private Integer notes;//注单量
    private String betTotalMoney;//总投注额
    private String betEffMoney;//总有效投注额
    private String gains;//损益

    public Integer getBets() {
        return bets;
    }

    public void setBets(Integer bets) {
        this.bets = bets;
    }

    public Integer getNotes() {
        return notes;
    }

    public void setNotes(Integer notes) {
        this.notes = notes;
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
