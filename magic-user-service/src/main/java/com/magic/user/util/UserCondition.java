package com.magic.user.util;

import com.alibaba.fastjson.JSONObject;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 0:01
 *
 * @Dcoc 某层级下会员查询
 * { "currencyType":1,//币种
 * "account":{//账号
 * "type":1,//类型
 * "username":""//账号名
 * },
 * "status":1,//状态
 * "level":1,//层级
 * "register":{
 * "start":1482920192812,//开始时间
 * "end":1483920192813//结束时间
 * },
 * "depositCount":{ //存款次数区间
 * "min":100,
 * "max":200
 * },
 * "withdrawCount":{//取款次数区间
 * "min":100,
 * "max":200
 * },
 * "depositMoney":{ //充值总额区间
 * "min":100,
 * "max":200
 * },
 * "withdrawMoney":{//提款总额区间
 * "min":100,
 * "max":200
 * }
 * }
 */

/**
 * @Doc 会员列表查询
 * {
 * "currencyType":1,//币种
 * "account":{//账号
 * "type":1,//类型
 * "username":""//账号名
 * },
 * "status":1,//状态
 * "level":1,//层级
 * "register":{
 * "start":1482920192812,//开始时间
 * "end":1483920192813//结束时间
 * },
 * "depositCount":{ //存款次数区间
 * "min":100,
 * "max":200
 * },
 * "withdrawCount":{//取款次数区间
 * "min":100,
 * "max":200
 * }
 * }
 */
public class UserCondition {


    private int currencyType;
    private JSONObject account; ////账号
    private int status; //状态
    private int level;  //层级
    private JSONObject register;    //注册时间
    private JSONObject depositCount;//存款次数区间
    private JSONObject withdrawCount;//取款次数区间
    private JSONObject depositMoney;//充值总额区间
    private JSONObject withdrawMoney;   //提款总额区间

    public int getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(int currencyType) {
        this.currencyType = currencyType;
    }

    public JSONObject getAccount() {
        return account;
    }

    public void setAccount(JSONObject account) {
        this.account = account;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public JSONObject getRegister() {
        return register;
    }

    public void setRegister(JSONObject register) {
        this.register = register;
    }

    public JSONObject getDepositCount() {
        return depositCount;
    }

    public void setDepositCount(JSONObject depositCount) {
        this.depositCount = depositCount;
    }

    public JSONObject getWithdrawCount() {
        return withdrawCount;
    }

    public void setWithdrawCount(JSONObject withdrawCount) {
        this.withdrawCount = withdrawCount;
    }

    public JSONObject getDepositMoney() {
        return depositMoney;
    }

    public void setDepositMoney(JSONObject depositMoney) {
        this.depositMoney = depositMoney;
    }

    public JSONObject getWithdrawMoney() {
        return withdrawMoney;
    }

    public void setWithdrawMoney(JSONObject withdrawMoney) {
        this.withdrawMoney = withdrawMoney;
    }
}
