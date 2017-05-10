package com.magic.user.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 15:17
 *
 * @Doc 账户类型
 */
public enum AccountType implements IEnum<AccountType> {

    proprietor(1, "业主"),  //业主
    stockholder(2, "股东"), //股东
    agent(3, "代理"),   //代理
    worker(4, "子账号"),  //子账号/工作人员
    member(5, "会员");  //会员

    private static Map<Integer, AccountType> maps = new HashMap<>();

    static {
        for (AccountType type : AccountType.values())
            maps.put(type.value(), type);
    }

    private int value;
    private String desc;

    AccountType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public int value() {
        return value;
    }

    public String desc() {
        return desc;
    }

    public static AccountType parse(int value) {
        return maps.get(value);
    }

    public static String getDesc(AccountType type) {
        String desc = "";
        switch (type) {
            case proprietor:
                desc = "业主";
                break;
            case stockholder:
                desc = "股东";
                break;
            case agent:
                desc = "代理";
                break;
            case worker:
                desc = "子账号";
                break;
            case member:
                desc = "会员";
                break;
            default:
                break;
        }
        return desc;
    }
}
