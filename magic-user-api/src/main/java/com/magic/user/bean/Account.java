package com.magic.user.bean;

import java.util.List;

/**
 * Account
 *
 * @author zj
 * @date 2017/5/6
 */
public class Account {

    /**
     * 类型
     */
    private Integer type;
    /**
     * 账号名
     */
    private String name;

    /**
     * nameList 解析,从name解析
     */
    private List<String> nameList;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getNameList() {
        return nameList;
    }

    public void setNameList(List<String> nameList) {
        this.nameList = nameList;
    }
}
