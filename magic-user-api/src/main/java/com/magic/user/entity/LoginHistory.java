package com.magic.user.entity;

import com.magic.user.enums.LoginType;

/**
 * @Doc 用户登录历史记录
 */
public class LoginHistory {
    private Long id;

    private Long userId;    //用户id

    private Long createTime;    //创建时间

    private Integer requestIp;  //来源ip

    private LoginType loginType;  //登录登出

    private String platform;    //操作设备

    private String ipAddr;  //ip所属地


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(Integer requestIp) {
        this.requestIp = requestIp;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }
}