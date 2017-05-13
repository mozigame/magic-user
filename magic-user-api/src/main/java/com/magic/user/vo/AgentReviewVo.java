package com.magic.user.vo;

/**
 * User: joey
 * Date: 2017/5/12
 * Time: 16:11
 *
 * @Doc 代理审核基础信息
 */
public class AgentReviewVo {

    private Long id;    //代理id
    private String account; //代理账号
    private String realname;//真实姓名
    private String telephone;//电话
    private String email;   //电子邮箱
    private String bankCardNo;  //银行卡号

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }
}
