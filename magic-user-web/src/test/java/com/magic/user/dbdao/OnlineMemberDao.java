package com.magic.user.dbdao;

/**
 * OnlineMemberDao
 *
 * @author zj
 * @date 2017/5/10
 */
public interface OnlineMemberDao {

    /**
     * 查询
     * @param memberId
     * @return
     */
    OnLineMember findById(long memberId);
}
