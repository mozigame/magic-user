package com.magic.user.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 15:20
 *
 * @Doc 账号审核状态
 */
public enum ReviewStatus implements IEnum {

    noReview(1, "未审核"),    //未审核
    noPass(2, "拒绝"),  //已拒绝
    pass(3, "通过");    //已通过


    private static Map<Integer, ReviewStatus> maps = new HashMap<>();

    static {
        for (ReviewStatus status : ReviewStatus.values())
            maps.put(status.value(), status);
    }

    private int value;
    private String desc;

    ReviewStatus(int value, String desc) {
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

    public static ReviewStatus parse(int value) {
        return maps.get(value);
    }

}
