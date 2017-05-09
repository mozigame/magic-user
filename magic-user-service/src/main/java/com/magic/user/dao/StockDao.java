package com.magic.user.dao;

import com.magic.api.commons.atlas.core.mybatis.MyBatisDaoImpl;
import com.magic.user.entity.User;
import org.springframework.stereotype.Component;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 17:07
 */
@Component
public class StockDao extends MyBatisDaoImpl<User, Long> {
}
