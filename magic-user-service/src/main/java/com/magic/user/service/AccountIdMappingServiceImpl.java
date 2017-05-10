package com.magic.user.service;

import org.springframework.stereotype.Service;

/**
 * User: joey
 * Date: 2017/5/10
 * Time: 10:42
 */
@Service("accountIdMappingService")
public class AccountIdMappingServiceImpl implements AccountIdMappingService {

    @Override
    public long getUid(long ownerId, String account) {
        return 0;
    }
}
