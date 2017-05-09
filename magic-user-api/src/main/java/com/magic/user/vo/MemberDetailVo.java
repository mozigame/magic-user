package com.magic.user.vo;

/**
 * MemberDetailVo
 *
 * @author zj
 * @date 2017/5/8
 */
public class MemberDetailVo {

    /**
     * 会员基础信息
     */
    private MemberInfo baseInfo;
    /**
     * 会员优惠方案
     */
    private MemberPreferScheme preferScheme;
    /**
     * 会员资金概况
     */
    private FundProfile<MemberFundInfo> fundProfile;
    /**
     * 投注记录
     */
    private MemberBetHistory betHistory;
    /**
     * 优惠记录
     */
    private MemberDiscountHistory discountHistory;

    public MemberInfo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(MemberInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    public MemberPreferScheme getPreferScheme() {
        return preferScheme;
    }

    public void setPreferScheme(MemberPreferScheme preferScheme) {
        this.preferScheme = preferScheme;
    }

    public FundProfile getFundProfile() {
        return fundProfile;
    }

    public void setFundProfile(FundProfile fundProfile) {
        this.fundProfile = fundProfile;
    }

    public MemberBetHistory getBetHistory() {
        return betHistory;
    }

    public void setBetHistory(MemberBetHistory betHistory) {
        this.betHistory = betHistory;
    }

    public MemberDiscountHistory getDiscountHistory() {
        return discountHistory;
    }

    public void setDiscountHistory(MemberDiscountHistory discountHistory) {
        this.discountHistory = discountHistory;
    }
}
