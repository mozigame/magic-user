package com.magic.user.service;

import com.magic.user.entity.OwnerAccountUser;

/**
 * AccountIdMappingService
 *
 * @author zj
 * @date 2017/5/9
 */
public interface AccountIdMappingService {
    /**
     * 根据联合主键查询股东或代理ID
     *
     * @param ownerId 业主ID
     * @param account 股东/代理账号名
     * @return
     */
    long getUid(Long ownerId, String account);

    /**
     * 添加
     * @param ownerAccountUser
     * @return
     */
    long add(OwnerAccountUser ownerAccountUser);
}
