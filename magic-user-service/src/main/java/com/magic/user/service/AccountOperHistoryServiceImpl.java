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
    public long getCount(Integer type, String account, Long uid) {
        return (long) accountOperHistoryDbService.get("getCount", new String[]{"type", "account", "procUserId"}, new Object[]{type, account, uid});
    }

    @Override
    public long add(AccountOperHistory operHistory) {
        return accountOperHistoryDbService.insert(operHistory);
    }

    @Override
    public List<AccountOperHistory> getList(Integer type, String account, Long uid, Integer page, Integer count) {
        Integer offset = (page - 1) * count;
        return accountOperHistoryDbService.find("findbyPage", new String[]{"type", "account", "procUserId", "offset", "limit"}, new Object[]{type, account, uid, offset, count});
    }

    @Override
    public List<AccountOperHistory> getList(Integer type, String account, Long uid) {
        return accountOperHistoryDbService.find("findbyPage", new String[]{"type", "account", "procUserId"}, new Object[]{type, account, uid});
    }
}
