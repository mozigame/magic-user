package com.magic.user.storage;

import com.magic.api.commons.atlas.core.BaseDao;
import com.magic.user.dao.AccountOperHistoryDao;
import com.magic.user.entity.AccountOperHistory;
import com.magic.user.storage.base.BaseDbServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:49
 */
@Service("accountOperHistoryDbService")
public class AccountOperHistoryDbService extends BaseDbServiceImpl<AccountOperHistory, Long> {

    @Resource
    private AccountOperHistoryDao accountOperHistoryDao;

    @Override
    public BaseDao<AccountOperHistory, Long> getDao() {
        return accountOperHistoryDao;
    }
}
