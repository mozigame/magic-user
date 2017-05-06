package com.magic.user.dao;

import com.magic.api.commons.atlas.core.mybatis.MyBatisDaoImpl;
import com.magic.user.entity.LoginHistory;
import org.springframework.stereotype.Component;

@Component
public class LoginHistoryDao extends MyBatisDaoImpl<LoginHistory, Long> {

}