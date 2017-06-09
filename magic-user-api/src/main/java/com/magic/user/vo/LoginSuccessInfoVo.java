package com.magic.user.vo;

import java.util.Collection;

/**
 * LoginSuccessInfoVo
 *
 * @author morton
 * @date 2017/6/7
 */
public class LoginSuccessInfoVo {

    private Long id;//用户ID
    private String username;//用户名
    private String balance;//用户余额
    private Long message;//未读消息数量
    private String token;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public Long getMessage() {
        return message;
    }

    public void setMessage(Long message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
