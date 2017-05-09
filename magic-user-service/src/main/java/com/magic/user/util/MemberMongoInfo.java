package com.magic.user.util;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 15:26
 */
@Document(collection = "member_info")
public class MemberMongoInfo {

    private long memberId;  //会员id
    private int currencyType;//币种
    private String memberName;    //账号名
    private int status;//状态
    private int level;//层级
    private long registerTime;    //注册时间
    private int depositCount;//存款次数
    private long withdrawCount;//取款次数
    private long depositMoney;//提款总额
    private long withdrawMoney;//充值总额
    private long agentId;//代理id
    private String agentName;
    private long stockId;//股东id
    private String stockName;   //股东名称
    private long ownerId;//业主id
    private int isDelete;//是否删除
}
