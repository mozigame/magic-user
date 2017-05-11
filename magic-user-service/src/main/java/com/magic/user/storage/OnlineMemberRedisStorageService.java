package com.magic.user.storage;

/**
 * OnlineMemberRedisStorageService
 *
 * @author zj
 * @date 2017/5/10
 */
public interface OnlineMemberRedisStorageService {
    /**
     * 保存在线会员
     * @param ownerId
     * @param memberId
     * @return
     */
    boolean addOnlineMember(Long ownerId, Long memberId);
}
