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
public enum LoginType implements IEnum {

    logout(1, "退出"),  //退出
    login(2, "登录");   //登录


    private static Map<Integer, LoginType> maps = new HashMap<>();

    static {
        for (LoginType type : LoginType.values())
            maps.put(type.value(), type);
    }

    private int value;
    private String desc;

    LoginType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int value() {
        return value;
    }

    public String desc() {
        return desc;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static LoginType parse(int value) {
        return maps.get(value);
    }
}
