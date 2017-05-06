package com.magic.user.storage;

import com.magic.api.commons.atlas.core.BaseDao;
import com.magic.user.dao.MemberDao;
import com.magic.user.entity.Member;
import com.magic.user.storage.base.BaseDbServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:28
 */
@Service("memberDbService")
public class MemberDbService extends BaseDbServiceImpl<Member, Long> {

    @Resource
    private MemberDao memberDao;

    @Override
    public BaseDao<Member, Long> getDao() {
        return memberDao;
    }
}
