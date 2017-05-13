package com.magic.user.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 0:01
 **/
public class UserCondition {


    private String account; ////账号
    private Register register;    //注册时间
    private String promotionCode;  //推广码
    private int status; //状态
    private RegionNumber members; //会员数
    private RegionNumber depositMoney;//存款金额区间
    private RegionNumber withdrawMoney;   //提款总额区间

    /**
     * 空对象
     */
    private static final UserCondition EMPTY_USER_CONDITION = new UserCondition();

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
        this.register = register;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public RegionNumber getMembers() {
        return members;
    }

    public void setMembers(RegionNumber members) {
        this.members = members;
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

    public String getPromotionCode() {
        return promotionCode;
    }

    public void setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
    }

    /**
     * 反序列化
     *
     * @param condition
     * @return
     */
    public static UserCondition valueOf(String condition) {
        try {
            return JSONObject.parseObject(condition, UserCondition.class);
        } catch (JSONException e) {
            ApiLogger.error(String.format("parse conditon to usercondition object error. condition: %s, msg: %s", condition, e.getMessage()));
        }
        return EMPTY_USER_CONDITION;
    }
}
