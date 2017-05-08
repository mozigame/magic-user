package com.magic.user.storage;

import com.magic.user.util.UserVo;
import com.magic.api.commons.model.Page;
import org.springframework.data.domain.PageRequest;

/**
 * User: joey
 * Date: 2017/5/4
 * Time: 22:08
 */
public interface UserMongoService {

    int insert(UserVo userVo);

    int update(UserVo userVo);

    Page<UserVo> findPage(PageRequest page, final String[] paramName, final String[] sign, final Object... values);

}
