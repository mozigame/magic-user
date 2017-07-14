package com.magic.user.vo;

import java.io.Serializable;

/**
 * Created by liaozhilong on 2017/7/14.
 */
public class BankDetailVo implements Serializable {
    private String realname;
    private String bank;
    private String bankCardNo;  //银行卡号
    private boolean isHave;

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
