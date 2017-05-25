package com.magic.user.service;

import com.magic.user.constants.UserContants;
import com.magic.user.entity.OwnerAccountUser;
import com.magic.user.storage.OwnerAccountUserDbService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/10
 * Time: 10:42
 */
@Service("accountIdMappingService")
public class AccountIdMappingServiceImpl implements AccountIdMappingService {

    @Resource(name = "ownerAccountUserDbService")
    private OwnerAccountUserDbService ownerAccountUserDbService;

    @Override
    public long getUid(Long ownerId, String account) {
        Long uid = (Long) ownerAccountUserDbService.get("getUid", new String[]{"account"}, ownerId + UserContants.SPLIT_LINE + account);
        return uid == null ? 0 : uid;
    }

    @Override
    public long add(OwnerAccountUser ownerAccountUser) {
        return ownerAccountUserDbService.insert(ownerAccountUser);
    }

}
