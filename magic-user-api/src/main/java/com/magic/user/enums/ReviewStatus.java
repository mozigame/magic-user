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
public enum ReviewStatus {

    noReview(1),    //未审核
    noPass(2),  //已拒绝
    pass(3);    //已通过


    private static Map<Integer, ReviewStatus> maps = new HashMap<>();

    static {
        for (ReviewStatus status : ReviewStatus.values())
            maps.put(status.value(), status);
    }

    private int value;

    ReviewStatus(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static ReviewStatus parse(int value) {
        return maps.get(value);
    }

}
