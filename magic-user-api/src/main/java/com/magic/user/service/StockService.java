package com.magic.user.service;

import com.magic.user.entity.User;

import java.util.List;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 17:19
 */
public interface StockService {

    List<Map<String, Object>> findAll();

    Map<String, Object> getDetail(long id);

    int updatePwd(String pwd, long id);

    int update(User user);

    Long add(User user);

    int disable(long id, int status);


}
