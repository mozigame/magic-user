package com.magic.user.service.dubbo;

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

}
