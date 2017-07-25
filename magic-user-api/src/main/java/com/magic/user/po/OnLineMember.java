package com.magic.user.po;

/**
 * OnLineMember
 *
 * @author zj
 * @date 2017/5/10
 */
public class OnLineMember {

    private Long memberId;//会员ID
    private String account;//会员账号
    private Long ownerId;//业主ID
    private Long holderId;//股东ID
    private Long agentId;//代理ID
    private Long registerTime;//注册时间
    private String registerIp;//注册IP
    private Long loginTime;//登陆时间
    private String loginIp;//登陆ip
    private Integer status;//状态 1注销 2登录
    private String city;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getHolderId() {
        return holderId;
    }

    public void setHolderId(Long holderId) {
        this.holderId = holderId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Long registerTime) {
        this.registerTime = registerTime;
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(String registerIp) {
        this.registerIp = registerIp;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Integer getStatus() {
        return status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
