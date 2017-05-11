package com.magic.user.vo;

/**
 * OnLineMemberVo
 *
 * @author zj
 * @date 2017/5/11
 */
public class OnLineMemberVo {

    private Long memberId;//会员ID
    private String account;//会员账号
    private String registerTime;//注册时间
    private String registerIp;//注册IP
    private String loginTime;//登陆时间
    private String loginIp;//登陆ip

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

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(String registerIp) {
        this.registerIp = registerIp;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }
}
