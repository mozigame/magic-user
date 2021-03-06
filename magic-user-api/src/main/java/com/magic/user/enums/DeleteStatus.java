package com.magic.user.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 15:30
 *
 * @Doc 删除状态
 */
public enum DeleteStatus implements IEnum<DeleteStatus> {

    noDel(1, "未删除"),   //未删除
    del(2, "删除"); //删除


    private static Map<Integer, DeleteStatus> maps = new HashMap<>();

    static {
        for (DeleteStatus status : DeleteStatus.values())
            maps.put(status.value(), status);
    }

    private int value;

    private String desc;

    DeleteStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public int value() {
        return value;
    }

    public String desc() {
        return desc;
    }


    public static DeleteStatus parse(int value) {
        return maps.get(value);
    }
}
