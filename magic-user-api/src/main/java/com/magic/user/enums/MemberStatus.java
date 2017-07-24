package com.magic.user.enums;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User: jac
 * Date: 2017/7/24
 *
 * @Doc 会员帐号状态
 */
public enum MemberStatus implements Serializable {

    login(1, "登陆"),//已登录
    logout(2, "注销"),//已退出
    enable(3, "启用"),  //启用
    disable(4, "停用"); //停用

    private int value;
    private String desc;

    private static Map<Integer, MemberStatus> maps = new HashMap<>();

    static {
        for (MemberStatus status : MemberStatus.values())
            maps.put(status.value(), status);
    }

    MemberStatus(int value, String desc) {
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

    public static MemberStatus parse(int value) {
        return maps.get(value);
    }

}
