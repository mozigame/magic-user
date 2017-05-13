package com.magic.user.storage;

import com.magic.api.commons.atlas.core.BaseDao;
import com.magic.user.dao.OwnerAccountUserDao;
import com.magic.user.entity.OwnerAccountUser;
import com.magic.user.storage.base.BaseDbServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: joey
 * Date: 2017/5/11
 * Time: 14:41
 */
@Service("ownerAccountUserDbService")
public class OwnerAccountUserDbService extends BaseDbServiceImpl<OwnerAccountUser, Long> {

    @Autowired
    private OwnerAccountUserDao ownerAccountUserDao;

    @Override
    public BaseDao<OwnerAccountUser, Long> getDao() {
        return ownerAccountUserDao;
    }
}
