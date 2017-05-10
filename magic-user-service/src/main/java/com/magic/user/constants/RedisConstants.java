package com.magic.user.constants;

/**
 * RedisConstants
 *
 * @author zj
 * @date 2017/5/10
 */
public class RedisConstants {

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
    public static String assembleOwnerMembersList(long ownerId){
        return assemble(OWNER_MEMBERS_LIST, ownerId);
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
