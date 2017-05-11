package com.magic.user.entity;

/**
 * OnlineMemberConditon
 *
 * @author zj
 * @date 2017/5/10
 */
public class OnlineMemberConditon {

    private Long memberId;//会员ID
    private Long ownerId;//业主ID
    private Long agentId;//代理ID
    private Long holderId;//股东ID
    private Long registerStartTime;//注册开始时间
    private Long registerEndTime;//注册结束时间
    private Long loginStartTime;//登陆开始时间
    private Long loginEndTime;//登陆结束时间
    private Integer status;//会员登陆状态

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Long getHolderId() {
        return holderId;
    }

    public void setHolderId(Long holderId) {
        this.holderId = holderId;
    }

    public Long getRegisterStartTime() {
        return registerStartTime;
    }

    public void setRegisterStartTime(Long registerStartTime) {
        this.registerStartTime = registerStartTime;
    }

    public Long getRegisterEndTime() {
        return registerEndTime;
    }

    public void setRegisterEndTime(Long registerEndTime) {
        this.registerEndTime = registerEndTime;
    }

    public Long getLoginStartTime() {
        return loginStartTime;
    }

    public void setLoginStartTime(Long loginStartTime) {
        this.loginStartTime = loginStartTime;
    }

    public Long getLoginEndTime() {
        return loginEndTime;
    }

    public void setLoginEndTime(Long loginEndTime) {
        this.loginEndTime = loginEndTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
