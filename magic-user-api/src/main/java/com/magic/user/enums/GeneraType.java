package com.magic.user.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 15:32
 */
public enum GeneraType {

    male(1),
    female(2);


    private static Map<Integer, GeneraType> maps = new HashMap<>();

    static {
        for (GeneraType type : GeneraType.values())
            maps.put(type.value(), type);
    }

    private int value;


    GeneraType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static GeneraType parse(int value) {
        return maps.get(value);
    }
}
