package com.magic.user.vo;

import java.io.Serializable;

/**
 * Created by liaozhilong on 2017/7/14.
 */
public class BankDetailVo implements Serializable {
    private String realname;
    private String bank;
    private String bankCardNo;  //银行卡号
    private String telephone;   //电话
    private boolean isHave;
    private String bankDetail;
    private String bankCode;

    public String getBankDetail() {
        return bankDetail;
    }

    public void setBankDetail(String bankDetail) {
        this.bankDetail = bankDetail;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public boolean isHave() {
        return isHave;
    }

    public void setHave(boolean have) {
        isHave = have;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }
}
