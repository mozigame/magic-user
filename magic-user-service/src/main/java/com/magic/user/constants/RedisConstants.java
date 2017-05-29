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
        USER_BASE_INFO("ubi_", 60 * 60 * 24); //业主平台用户基础信息

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
