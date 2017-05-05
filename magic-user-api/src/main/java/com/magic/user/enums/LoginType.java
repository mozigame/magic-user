package com.magic.user.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 15:23
 *
 * @Doc 登录类型
 */
public enum LoginType {

    logout(1),  //退出
    login(2);   //登录


    private static Map<Integer, LoginType> maps = new HashMap<>();

    static {
        for (LoginType type : LoginType.values())
            maps.put(type.value(), type);
    }

    private int value;

    LoginType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static LoginType parse(int value) {
        return maps.get(value);
    }
}
