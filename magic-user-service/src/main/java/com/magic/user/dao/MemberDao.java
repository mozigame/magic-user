package com.magic.user.dao;

import com.magic.api.commons.atlas.core.mybatis.MyBatisDaoImpl;
import com.magic.user.entity.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberDao extends MyBatisDaoImpl<Member, Long> {

}