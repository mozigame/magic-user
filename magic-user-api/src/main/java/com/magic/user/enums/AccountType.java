package com.magic.user.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 15:17
 *
 * @Doc 账户类型
 */
public enum AccountType {

    proprietor(1),  //业主
    stockholder(2), //股东
    agent(3),   //代理
    worker(4),  //子账号/工作人员
    member(5);  //会员

    private static Map<Integer, AccountType> maps = new HashMap<>();

    static {
        for (AccountType type : AccountType.values())
            maps.put(type.value(), type);
    }

    private int value;

    AccountType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static AccountType parse(int value) {
        return maps.get(value);
    }
}
