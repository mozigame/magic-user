package com.magic.user.bean;

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
     * 账号id
    */
    private Long id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
