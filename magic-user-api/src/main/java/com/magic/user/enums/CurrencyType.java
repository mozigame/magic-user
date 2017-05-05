package com.magic.user.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 16:15
 *
 * @Doc 币种
 */
public enum CurrencyType {

    CNY(1); //人民币


    private static Map<Integer, CurrencyType> map = new HashMap<>();

    static {
        for (CurrencyType type : CurrencyType.values())
            map.put(type.value(), type);
    }

    private int value;

    CurrencyType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static CurrencyType parse(int value) {
        return map.get(value);
    }

}
