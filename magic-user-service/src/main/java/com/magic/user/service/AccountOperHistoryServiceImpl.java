package com.magic.user.service;

import com.magic.user.entity.AccountOperHistory;
import com.magic.user.storage.AccountOperHistoryDbService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * User: joey
 * Date: 2017/5/11
 * Time: 15:19
 */
@Service("accountOperHistoryService")
public class AccountOperHistoryServiceImpl implements AccountOperHistoryService {

    @Resource
    private AccountOperHistoryDbService accountOperHistoryDbService;

    @Override
    public long getCount(Integer type, String account, Long uid, Long ownerId) {
        Long result = (Long) accountOperHistoryDbService.get("getCount", new String[]{"type", "account", "procUserId", "ownerId"}, new Object[]{type, account, uid, ownerId});
        return result == null ? 0 : result ;
    }

    @Override
    public long add(AccountOperHistory operHistory) {
        return accountOperHistoryDbService.insert(operHistory);
    }

    @Override
    public List<AccountOperHistory> getList(Integer type, String account, Long uid, Long ownerId, Integer page, Integer count) {
        Integer offset = page == null ? null : (page - 1) * count;
        return accountOperHistoryDbService.find("findbyPage", new String[]{"type", "account", "procUserId", "ownerId", "offset", "limit"}, new Object[]{type, account, uid, ownerId, offset, count});
    }

}
