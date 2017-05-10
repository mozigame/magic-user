package com.magic.user.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 15:32
 */
public enum GeneraType implements IEnum {

    male(1, "男"),
    female(2, "女");


    private static Map<Integer, GeneraType> maps = new HashMap<>();

    static {
        for (GeneraType type : GeneraType.values())
            maps.put(type.value(), type);
    }

    private int value;
    private String desc;

    GeneraType(int value, String desc) {
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

    public static GeneraType parse(int value) {
        return maps.get(value);
    }
}
