package com.magic.user.service;

import com.magic.user.entity.OwnerStockAgentMember;

/**
 * User: joey
 * Date: 2017/5/9
 * Time: 10:42
 */
public interface OwnerStockAgentService {

    /**
     * @param ownerStockAgentMember
     * @Doc 添加业主股东代理用户数映射
     */
    long add(OwnerStockAgentMember ownerStockAgentMember);
}
