package com.magic.user.storage;

import com.magic.api.commons.atlas.core.BaseDao;
import com.magic.user.dao.OwnerStockAgentDao;
import com.magic.user.entity.OwnerStockAgentMember;
import com.magic.user.storage.base.BaseDbServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/9
 * Time: 10:44
 */
@Service("ownerStockAgentDbService")
public class OwnerStockAgentDbService extends BaseDbServiceImpl<OwnerStockAgentMember, Long> {

    @Resource
    private OwnerStockAgentDao ownerStockAgentDao;

    @Override
    public BaseDao<OwnerStockAgentMember, Long> getDao() {
        return ownerStockAgentDao;
    }


}
