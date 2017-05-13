package com.magic.user.entity;

import com.magic.user.enums.LoginType;

/**
 * @Doc 登录基础信息
 */
public class Login {
    private Long id;

    private Long userId;    //用户id

    private String username;    //账号

    private String password;    //密码

    private Long updateTime;    //修改时间，最后登录时间

    private Integer lastLoginIp;    //最后登录ip

    private LoginType status; //登录等出

    public Login(){}
    public Login(Long userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    public Login(Long updateTime, Integer lastLoginIp, LoginType status) {
        this.updateTime = updateTime;
        this.lastLoginIp = lastLoginIp;
        this.status = status;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(Integer lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public LoginType getStatus() {
        return status;
    }

    public void setStatus(LoginType status) {
        this.status = status;
    }
}