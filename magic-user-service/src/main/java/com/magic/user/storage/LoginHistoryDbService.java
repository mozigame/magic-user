package com.magic.user.storage;

import com.magic.api.commons.atlas.core.BaseDao;
import com.magic.user.dao.LoginHistoryDao;
import com.magic.user.entity.LoginHistory;
import com.magic.user.entity.User;
import com.magic.user.storage.base.BaseDbServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:40
 */
@Service("loginHistoryDbService")
public class LoginHistoryDbService extends BaseDbServiceImpl<LoginHistory, Long> {

    @Resource
    private LoginHistoryDao loginHistoryDao;

    @Override
    public BaseDao<LoginHistory, Long> getDao() {
        return loginHistoryDao;
    }
}
