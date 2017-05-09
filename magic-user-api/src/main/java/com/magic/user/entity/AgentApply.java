package com.magic.user.entity;

import com.magic.user.enums.ReviewStatus;

import java.util.Date;

/**
 * @Doc 代理申请基础信息
 */
public class AgentApply {
    private long id;

    private String username;    //申请人账号

    private String realname;    //真实姓名

    private long stockId;   //股东id

    private String stockName;   //股东账号

    private String bankCardNo;  //银行卡号

    private String telephone;   //电话

    private String email;   //电子邮箱

    private ReviewStatus status;    //审核状态

    private String resourceUrl; //加入来源

    private int registerIp; //注册ip

    private Date createTime; //注册时间

    private String temp1;

    private String temp2;

    public AgentApply() {
    }

    public AgentApply(String username, long stockId, String telephone, String email, ReviewStatus status, String resourceUrl, int registerIp, Date createTime) {
        this.username = username;
        this.stockId = stockId;
        this.telephone = telephone;
        this.email = email;
        this.status = status;
        this.resourceUrl = resourceUrl;
        this.registerIp = registerIp;
        this.createTime = createTime;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
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

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
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