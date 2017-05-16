package com.magic.user.service.dubbo;

import com.magic.user.entity.Member;
import com.magic.user.entity.User;

/**
 * AccountDubboService
 *
 * @author zj
 * @date 2017/5/15
 */
public interface AccountDubboService {

    /**
     * 获取业主ID
     * @param uid 股东或代理ID
     * @return
     */
    long getOwnerId(long uid);

    /**
     * 获取用户
     * @param uid 股东或代理ID
     * @return
     */
    User getUser(long uid);

    /**
     * 获取会员
     * @param uid
     * @return
     */
    Member getMember(long uid);

}
