package com.magic.user.constants;

/**
 * UserContants
 *
 * @author Jake
 * @date 2017/5/8
 */
public class UserContants {

    public static final String EMPTY_STRING = "{}";//空字符串
    public static final String EMPTY_LIST = "{\"list\":[]}";//空list

    public static final String SPLIT_LINE = "-";    //业主id与用户账号连接符
    public static final String CALLER = "account"; //调用方
    public static final int EXPIRE_TIME = 86400; //设置过期时间 1天
    public static final String CLIENT_ID = "clientId";//客户端ID
    public static final int VERIFY_CODE_VALID_TIME = 300000;//验证码有效期 5分钟
    public static final int VERIFY_CODE_LENGTH = 4;//验证码长度

    public enum CUSTOMER_MQ_TAG {
        USER_ID_MAPPING_ADD,    //添加id映射表
        USER_ID_MAPPING_MODIFY, //修改id映射表
        AGENT_CONFIG_ADD,   //添加代理配置信息
        AGENT_CONDITION_ADD;   //添加代理基础信息到mongo

    }

}
