package com.magic.user.entity;

import com.magic.user.enums.ReviewStatus;

import java.util.Date;

/**
 * @Doc 代理申请基础信息
 */
public class AgentApply {
    private long id;

    private String username;    //申请人账号

    private long stockId;   //股东id

    private String telephone;   //电话

    private String email;   //电子邮箱

    private ReviewStatus status;    //审核状态

    private String resourceUrl; //加入来源

    private int registerIp; //注册ip

    private Date createTime; //注册时间

    private String temp1;

    private String temp2;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getStockId() {
        return stockId;
    }

    public void setStockId(long stockId) {
        this.stockId = stockId;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public int getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(int registerIp) {
        this.registerIp = registerIp;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getTemp1() {
        return temp1;
    }

    public void setTemp1(String temp1) {
        this.temp1 = temp1;
    }

    public String getTemp2() {
        return temp2;
    }

    public void setTemp2(String temp2) {
        this.temp2 = temp2;
    }
}