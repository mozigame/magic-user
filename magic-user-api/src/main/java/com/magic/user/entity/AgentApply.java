package com.magic.user.entity;

import com.magic.user.enums.ReviewStatus;

/**
 * @Doc 代理申请基础信息
 */
public class AgentApply {
    private Long id;

    private String username;    //申请人账号

    private String realname;    //真实姓名

    private String password;    //  密码

    private Long stockId;   //股东id

    private String stockName;   //股东账号

    private Long ownerId;   //业主id

    private String bankCardNo;  //银行卡号

    private String bank;    //银行名称

    private String bankDeposit; ///开户行

    private String telephone;   //电话

    private String email;   //电子邮箱

    private ReviewStatus status;    //审核状态

    private String resourceUrl; //加入来源

    private Integer registerIp; //注册ip

    private Long createTime; //注册时间

    private String province;//所属省

    private String city;//所属市

    private String weixin;//微信

    private String qq;//QQ

    private String temp1;

    private String temp2;

    public AgentApply() {
    }

    public AgentApply(String username, String realname, String password, Long stockId, String telephone, String email, ReviewStatus status, String resourceUrl, Integer registerIp, Long createTime) {
        this.username = username;
        this.realname = realname;
        this.password = password;
        this.stockId = stockId;
        this.telephone = telephone;
        this.email = email;
        this.status = status;
        this.resourceUrl = resourceUrl;
        this.registerIp = registerIp;
        this.createTime = createTime;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getRealname() {
        return realname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
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

    public Integer getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(Integer registerIp) {
        this.registerIp = registerIp;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBankDeposit() {
        return bankDeposit;
    }

    public void setBankDeposit(String bankDeposit) {
        this.bankDeposit = bankDeposit;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }
}