package com.magic.user.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.magic.api.commons.ApiLogger;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * MemberCondition
 *
 * @author zj
 * @date 2017/5/6
 */
public class MemberCondition {

    /**
     * 业主ID
     */
    private Long ownerId;

    /**
     * 代理ID
     */
    private Long agentId;
    /**
     * 币种
     */
    private Integer currencyType;
    /**
     * 账号
     */
    private Account account;

    /**
     * 账号状态
     */
    private Integer status;
    /**
     * 层级
     */
    private Integer level;

    /**
     * 多层级
     */
    private List<Integer> levelList;

    /**
     * 注册时间范围
     */
    private Register register;

    /**
     * 存款次数区间
     */
    private RegionNumber depositNumber;

    /**
     * 取款次数区间
     */
    private RegionNumber withdrawNumber;

    /**
     * 充值总额区间
     */
    private RegionNumber depositMoney;

    /**
     * 提款总额区间
     */
    private RegionNumber withdrawMoney;

    /**
     * 空对象
     */
    private static final MemberCondition EMPTY_MEMBER_CONDITION = new MemberCondition();

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Integer getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(Integer currencyType) {
        this.currencyType = currencyType;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
        this.register = register;
    }

    public RegionNumber getDepositNumber() {
        return depositNumber;
    }

    public void setDepositNumber(RegionNumber depositNumber) {
        this.depositNumber = depositNumber;
    }

    public RegionNumber getWithdrawNumber() {
        return withdrawNumber;
    }

    public void setWithdrawNumber(RegionNumber withdrawNumber) {
        this.withdrawNumber = withdrawNumber;
    }

    public RegionNumber getDepositMoney() {
        return depositMoney;
    }

    public void setDepositMoney(RegionNumber depositMoney) {
        this.depositMoney = depositMoney;
    }

    public RegionNumber getWithdrawMoney() {
        return withdrawMoney;
    }

    public void setWithdrawMoney(RegionNumber withdrawMoney) {
        this.withdrawMoney = withdrawMoney;
    }

    public static MemberCondition getEmptyMemberCondition() {
        return EMPTY_MEMBER_CONDITION;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public List<Integer> getLevelList() {
        return levelList;
    }

    public void setLevelList(List<Integer> levelList) {
        this.levelList = levelList;
    }




    /**
     * 反序列化
     *
     * @param condition
     * @return
     */
    public static MemberCondition valueOf(String condition) {
        try {
            MemberCondition memberCondition = JSON.parseObject(condition, MemberCondition.class);
            //希望后续判断采用list
            parseAccount(memberCondition);

            parseLevelList(memberCondition);
            return memberCondition;
        } catch (JSONException e) {
            ApiLogger.error(String.format("parse conditon to membercondition object error. condition: %s, msg: %s", condition, e.getMessage()));
            return MemberCondition.EMPTY_MEMBER_CONDITION;
        }

    }

    /**
     * 解析 levelList
     * @param memberCondition
     */
    private static void parseLevelList(MemberCondition memberCondition) {
        if (CollectionUtils.isEmpty(memberCondition.getLevelList())) {
            if (memberCondition.getLevel() != null) {
                List<Integer> levelList = new LinkedList<>();
                levelList.add(memberCondition.getLevel());
                memberCondition.setLevelList(levelList);
            }
        }
    }

    /**
     * 解析account
     * @param memberCondition
     */
    private static void parseAccount(MemberCondition memberCondition) {
        Account account = memberCondition.getAccount();
        if (account != null) {
            String name = account.getName();
            if (StringUtils.isNotBlank(name)) {
                String[] names = name.split(",");
                if (names != null && names.length > 0) {
                    List<String> nameList = new ArrayList<>(names.length);
                    for (String nameEle :names){
                        nameList.add(nameEle);
                    }
                    account.setNameList(nameList);
                }
            }else {

            }
        }
    }


    public static void main(String[] args) {
        String s = "{\"currencyType\":\"1\",\"status\":\"\",\"register\":{\"start\":\"\",\"end\":\"\"},\"account\":{\"type\":\"5\",\"name\":\"fdf66\"},\"depositNumber\":{\"min\":\"\",\"max\":\"\"},\"withdrawNumber\":{\"min\":\"\",\"max\":\"\"},\"depositMoney\":{\"min\":\"\",\"max\":\"\"},\"withdrawMoney\":{\"min\":\"\",\"max\":\"\"}}";
        MemberCondition s1 = valueOf(s);
        System.out.printf("1");
    }
}
