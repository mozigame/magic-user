package com.magic.user.entity;

import com.magic.user.enums.*;

import java.util.Date;

/**
 * @Doc 用户，股东，代理
 */
public class User {

    private long id;

    private long userId;   //代理id

    private String realname;    //真实姓名

    private String username;    //账号

    private String telephone;   //电话

    private String email;   //电子邮箱

    private Date registerTime;  //注册时间

    private int registerIp; //注册ip

    private String generalizeCode;  //推广码

    private GeneraType gender;  //性别

    private AccountStatus status;   //账号使用状态

    private CurrencyType currencyType;  //币种

    private String bankCardNo;  //银行卡号

    private String bank;    //银行信息

    private String bankDeposit; //开户行

    private long ownerId;  //业主id

    private DeleteStatus isDelete;  //是否删除

    private AccountType type;   //账号类型

    private String temp1;   //预留字符

    private String temp2;

    private String temp3;

    public User() {
    }

    /**
     * @param realname
     * @param username
     * @param telephone
     * @param email
     * @param gender
     * @param currencyType
     * @Doc 股东
     */
    public User(long userId, String realname, String username, String telephone, String email, AccountType type, GeneraType gender, CurrencyType currencyType, Date registerTime) {
        this.userId = userId;
        this.realname = realname;
        this.username = username;
        this.telephone = telephone;
        this.email = email;
        this.type = type;
        this.gender = gender;
        this.currencyType = currencyType;
        this.registerTime = registerTime;
    }

    /**
     * @param userId
     * @param realname
     * @param username
     * @param telephone
     * @param email
     * @param registerTime
     * @param registerIp
     * @param generalizeCode
     * @param status
     * @param bankCardNo
     * @Doc agent
     */
    public User(long userId, String realname, String username, String telephone, String email, AccountType type, Date registerTime, int registerIp, String generalizeCode, AccountStatus status, String bankCardNo) {
        this.userId = userId;
        this.realname = realname;
        this.username = username;
        this.telephone = telephone;
        this.email = email;
        this.type = type;
        this.registerTime = registerTime;
        this.registerIp = registerIp;
        this.generalizeCode = generalizeCode;
        this.status = status;
        this.bankCardNo = bankCardNo;
    }

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

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public int getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(int registerIp) {
        this.registerIp = registerIp;
    }

    public String getGeneralizeCode() {
        return generalizeCode;
    }

    public void setGeneralizeCode(String generalizeCode) {
        this.generalizeCode = generalizeCode;
    }

    public GeneraType getGender() {
        return gender;
    }

    public void setGender(GeneraType gender) {
        this.gender = gender;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(CurrencyType currencyType) {
        this.currencyType = currencyType;
    }

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public DeleteStatus getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(DeleteStatus isDelete) {
        this.isDelete = isDelete;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
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

    public String getTemp3() {
        return temp3;
    }

    public void setTemp3(String temp3) {
        this.temp3 = temp3;
    }
}