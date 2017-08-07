package com.magic.user.constants;

/**
 * RedisConstants
 *
 * @author zj
 * @date 2017/5/10
 */
public class RedisConstants {

    /**
     * @doc 组装业主平台用户的redis key
     */
    public enum USER_PREFIX {
        USER_BASE_INFO("ubi_", 86400); //业主平台用户基础信息

        private String prefix;  //
        private int expire;    //过期时间

        USER_PREFIX(String prefix, int expire) {
            this.prefix = prefix;
            this.expire = expire;
        }

        public String key(long id) {
            return prefix + id;
        }

        public int expire() {
            return expire;
        }
    }

    /**
     * 业主会员列表
     */
    private static final String OWNER_MEMBERS_LIST = "oml_";

    /**
     * 业主在线会员列表
     *
     * @param ownerId
     * @return
     */
    public static String assembleOwnerMembersList(long ownerId) {
        return assemble(OWNER_MEMBERS_LIST, ownerId);
    }

    /**
     * 业主下股东数量
     */
    private static final String OWNER_STOCK_NUM = "osn_";

    /**
     * 组装key
     * @param ownerId
     * @return
     */
    public static String assembleOwnerStockNum(long ownerId) {
        return assemble(OWNER_STOCK_NUM, ownerId);
    }

    /**
     * 业主下代理数量
     */
    private static final String OWNER_AGENT_NUM = "oan_";

    /**
     * 组装key
     * @param ownerId
     * @return
     */
    public static String assembleOwnerAgentNum(long ownerId) {
        return assemble(OWNER_AGENT_NUM, ownerId);
    }

    /**
     * 业主下会员数量
     */
    private static final String OWNER_MEMBER_NUM = "omn_";

    /**
     * 组装key
     * @param ownerId
     * @return
     */
    public static String assembleOwnerMemberNum(long ownerId) {
        return assemble(OWNER_MEMBER_NUM, ownerId);
    }

    /**
     * 业主下子账号数量
     */
    private static final String OWNER_WORKER_NUM = "own_";

    /**
     * 组装key
     * @param ownerId
     * @return
     */
    public static String assembleOwnerWorkerNum(long ownerId) {
        return assemble(OWNER_WORKER_NUM, ownerId);
    }

    /**
     * 记录业主或代理下登录用户
     */
    private static final String OWNER_AGENT_MEMBER_LOGINS = "oaml_";

    /**
     * 组装incr key
     * @param id
     * @param last
     * @return
     */
    public static String assmbleIncrKey(Long id, String last) {
        String prefix = OWNER_AGENT_MEMBER_LOGINS.concat(String.valueOf(id)).concat("_");
        return assemble(prefix, last);
    }

    /**
     * 业主某天登录用户数
     */
    private static final String OWNER_MEMBER_LOGINS = "omlogins_";

    /**
     * 业主维度某天登录用户数
     *
     * @param date
     * @return
     */
    public static String assmbleOwnerCountKey(String date) {
        return assemble(OWNER_MEMBER_LOGINS, date);
    }

    /**
     * 代理某天登录用户数
     */
    private static final String AGENT_MEMBER_LOGINS = "amlogins_";

    /**
     * 代理维度某天登录用户数
     *
     * @param date
     * @return
     */
    public static String assmbleAgentCountKey(String date) {
        return assemble(AGENT_MEMBER_LOGINS, date);
    }

    /**
     * 登录验证码
     */
    private static final String LOGIN_VERIFY_CODE = "ulvcode_";

    /**
     * 用户登录验证码
     * @param clientId
     * @return
     */
    public static String assembleVerifyCode(String clientId) {
        return assemble(LOGIN_VERIFY_CODE, clientId);
    }

    /**
     * 检查同一ip下注册次数
     * @param ip
     * @return
     */
    private static final String CHECK_REGISTER_IP="reg_suc_ip_";

    /**
     * 检查同一ip下注册次数
     * @param ip
     * @return
     */
    public static String assembleCheckRegisterIp(String ip) {
        return CHECK_REGISTER_IP + ip;
    }


    private static final String SAME_IP_LOGIN="s_ip_l_";

    public static String assembleLoginCount(Long ownerId,String username,String ip) {
        return SAME_IP_LOGIN + ownerId + "_" + username + "_" + ip;
    }
    /**
     * 组装key
     *
     * @param pre
     * @param last
     * @return
     */
    private static String assemble(String pre, Object last) {
        StringBuilder key = new StringBuilder(pre);
        key.append(last);
        return key.toString();
    }


}
