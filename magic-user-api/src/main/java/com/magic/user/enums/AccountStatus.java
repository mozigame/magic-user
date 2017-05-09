package com.magic.user.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 15:27
 *
 * @Doc 账号使用状态
 */
public enum AccountStatus {

    enable(1, "启用"),  //启用
    disable(2, "停用"); //停用

    private int value;
    private String desc;

    private static Map<Integer, AccountStatus> maps = new HashMap<>();

    static {
        for (AccountStatus status : AccountStatus.values())
            maps.put(status.value(), status);
    }

    AccountStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int value() {
        return value;
    }

    public String desc() {
        return desc;
    }

    public static AccountStatus parse(int value) {
        return maps.get(value);
    }

}
