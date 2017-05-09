package com.magic.user.vo;

/**
 * MemberLevelListVo
 *
 * @author zj
 * @date 2017/5/8
 */
public class MemberLevelListVo {

    private Integer id;//层级编号
    private String name;//层级名称
    private String createTime;//创建时间
    private Integer members;//会员人数
    private LevelCondition condition;//分层条件
    private Integer returnWater;//返水方案ID
    private String returnWaterName;//返水方案名
    private Integer discount;//出入款优惠方案ID
    private String discountName;//出入款优惠方案

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getMembers() {
        return members;
    }

    public void setMembers(Integer members) {
        this.members = members;
    }

    public LevelCondition getCondition() {
        return condition;
    }

    public void setCondition(LevelCondition condition) {
        this.condition = condition;
    }

    public Integer getReturnWater() {
        return returnWater;
    }

    public void setReturnWater(Integer returnWater) {
        this.returnWater = returnWater;
    }

    public String getReturnWaterName() {
        return returnWaterName;
    }

    public void setReturnWaterName(String returnWaterName) {
        this.returnWaterName = returnWaterName;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }
}
