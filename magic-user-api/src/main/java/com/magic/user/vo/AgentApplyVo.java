package com.magic.user.vo;

/**
 * User: joey
 * Date: 2017/5/12
 * Time: 16:06
 *
 * @Doc 代理申请基础信息
 */
public class AgentApplyVo {

    private Long id;    //代理id
    private Long holder;    //股东id
    private String holderName; //股东账号
    private String account; //代理账号
    private String realname;    //代理真实姓名
    private String telephone;   //电话
    private String email;   //电子邮箱
    private Integer status; //审核状态
    private String showStatus;  //状态描述
    private String source;  //加入来源
    private String registerIp;  //注册ip
    private String operUserName;    //操作人账号
    private String operatorTime;    //操作时间

    private String bankCardNo;  //银行卡号
    private String bank;    //银行信息
    private String bankDeposit; //开户行

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHolder() {
        return holder;
    }

    public void setHolder(Long holder) {
        this.holder = holder;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(String showStatus) {
        this.showStatus = showStatus;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(String registerIp) {
        this.registerIp = registerIp;
    }

    public String getOperUserName() {
        return operUserName;
    }

    public void setOperUserName(String operUserName) {
        this.operUserName = operUserName;
    }

    public String getOperatorTime() {
        return operatorTime;
    }

    public void setOperatorTime(String operatorTime) {
        this.operatorTime = operatorTime;
    }

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
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
}
