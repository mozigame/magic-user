package com.magic.user.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.magic.api.commons.ApiLogger;

/**
 * MemberCondition
 *
 * @author zj
 * @date 2017/5/6
 */
public class MemberCondition {

    /**
     * 币种
     */
    private Integer currencyType;
    /**
     * 账号
     */
    private Account account;
    /**
     * 账号状态
     */
    private Integer status;
    /**
     * 层级
     */
    private Integer level;

    /**
     * 注册时间范围
     */
    private Register register;

    /**
     * 存款次数区间
     */
    private RegionNumber depositNumber;

    /**
     * 取款次数区间
     */
    private RegionNumber withdrawNumber;

    /**
     * 充值总额区间
     */
    private RegionNumber depositMoney;

    /**
     * 提款总额区间
     */
    private RegionNumber withdrawMoney;

    /**
     * 空对象
     */
    private static final MemberCondition EMPTY_MEMBER_CONDITION = new MemberCondition();

    public Integer getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(Integer currencyType) {
        this.currencyType = currencyType;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
        this.register = register;
    }

    public RegionNumber getDepositNumber() {
        return depositNumber;
    }

    public void setDepositNumber(RegionNumber depositNumber) {
        this.depositNumber = depositNumber;
    }

    public RegionNumber getWithdrawNumber() {
        return withdrawNumber;
    }

    public void setWithdrawNumber(RegionNumber withdrawNumber) {
        this.withdrawNumber = withdrawNumber;
    }

    public RegionNumber getDepositMoney() {
        return depositMoney;
    }

    public void setDepositMoney(RegionNumber depositMoney) {
        this.depositMoney = depositMoney;
    }

    public RegionNumber getWithdrawMoney() {
        return withdrawMoney;
    }

    public void setWithdrawMoney(RegionNumber withdrawMoney) {
        this.withdrawMoney = withdrawMoney;
    }

    public static MemberCondition getEmptyMemberCondition() {
        return EMPTY_MEMBER_CONDITION;
    }

    /**
     * 反序列化
     *
     * @param condition
     * @return
     */
    public static MemberCondition valueOf(String condition){
        try {
            return JSON.parseObject(condition, MemberCondition.class);
        }catch (JSONException e){
            ApiLogger.error(String.format("parse conditon to membercondition object error. condition: %s, msg: %s", condition, e.getMessage()));
        }
        return EMPTY_MEMBER_CONDITION;
    }
}
