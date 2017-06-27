package com.magic.user.vo;

/**
 * MemberDetailVo
 *
 * @author zj
 * @date 2017/5/8
 */
public class AgentDetailVo {

    /**
     * 代理基础信息
     */
    private AgentInfoVo baseInfo;
    /**
     * 代理参数配置
     */
    private AgentConfigVo settings;
    /**
     * 优惠记录
     */
    private FundProfile<AgentFundInfo> fundProfile;

    public AgentInfoVo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(AgentInfoVo baseInfo) {
        this.baseInfo = baseInfo;
    }

    public AgentConfigVo getSettings() {
        return settings;
    }

    public void setSettings(AgentConfigVo settings) {
        this.settings = settings;
    }

    public FundProfile<AgentFundInfo> getFundProfile() {
        return fundProfile;
    }

    public void setFundProfile(FundProfile<AgentFundInfo> fundProfile) {
        this.fundProfile = fundProfile;
    }
}
