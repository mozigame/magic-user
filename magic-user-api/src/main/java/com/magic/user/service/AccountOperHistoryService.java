package com.magic.user.service;

import com.magic.user.entity.AccountOperHistory;

import java.util.List;

/**
 * User: joey
 * Date: 2017/5/5
 * Time: 23:23
 */
public interface AccountOperHistoryService {
    /**
     * 获取记录数，业主ID或操作人ID为UID的记录
     * @param type
     * @param account
     * @param uid 所属ID
     * @return
     */
    long getCount(Integer type, String account, Long uid);

    /**
     * 获取记录列表，业主ID或操作人ID为UID的记录
     * @param type
     * @param account
     * @param uid 所属ID
     * @param page
     * @param count
     * @return
     */
    List<AccountOperHistory> getList(Integer type, String account, Long uid ,int page, int count);

    /**
     * 获取记录列表，业主ID或操作人ID为UID的记录
     * @param type
     * @param account
     * @param uid
     * @return
     */
    List<AccountOperHistory> getList(Integer type, String account, Long uid);
}
