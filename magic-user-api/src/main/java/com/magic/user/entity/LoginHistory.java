package com.magic.user.entity;

import com.magic.user.enums.LoginType;

import java.util.Date;

/**
 * @Doc 用户登录历史记录
 */
public class LoginHistory {
    private long id;

    private long userId;    //用户id

    private Date createTime;    //创建时间

    private int requestIp;  //来源ip

    private LoginType loginType;  //登录登出

    private String platform;    //操作设备

    private String ipAddr;  //ip所属地

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getRequestIp() {
        return requestIp;
    }

    public void setRequestIp(int requestIp) {
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