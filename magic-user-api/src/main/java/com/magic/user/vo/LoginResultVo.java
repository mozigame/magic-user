package com.magic.user.vo;

import java.util.Date;

/**
 * LoginResultVo
 *
 * @author morton
 * @date 2017/6/29
 */
public class LoginResultVo {

    private Long userId;//用户ID
    private String username;//用户名
    private String balance;//用户余额
    private int message;//未读消息数量
    private int notReadNotice;//未读公告
    private String token;
    private int type;//是否显示授信
    private String limit;//授信额度
    private String limited;//已用额度
    private String time;//服务器时间
    private Long ownerId;//业主ID

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

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public int getNotReadNotice() {
        return notReadNotice;
    }

    public void setNotReadNotice(int notReadNotice) {
        this.notReadNotice = notReadNotice;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getLimited() {
        return limited;
    }

    public void setLimited(String limited) {
        this.limited = limited;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
