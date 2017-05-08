package com.magic.user.storage.impl;

import com.magic.api.commons.model.Page;
import com.magic.api.commons.utils.StringUtils;
import com.magic.user.dao.mongo.UserMongoDao;
import com.magic.user.storage.UserMongoService;
import com.magic.user.util.UserVo;
import com.magic.user.utils.OperaSign;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.springframework.data.mongodb.core.query.Criteria.*;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * User: joey
 * Date: 2017/5/6
 * Time: 15:52
 */
@Service("userMongoService")
public class UserMongoServiceImpl implements UserMongoService {

    @Resource
    private UserMongoDao userMongoDao;

    @Resource(name = "mongoTemplate")
    private MongoTemplate mongoTemplate;

    @Override
    public int insert(UserVo userVo) {
//        UserVo userVo = UserVo.parseUserVo(user);

        userMongoDao.save(userVo);
        return 0;
    }

    @Override
    public int update(UserVo userVo) {
        Update update = new Update();
        if (StringUtils.isNotBlank(userVo.getEmail())) {
            update.set("email", userVo.getEmail());
        }
        if (StringUtils.isNoneBlank(userVo.getRealname()))
            update.set("realname", userVo.getRealname());
        mongoTemplate.updateFirst(new Query(where("_id").is(userVo.getId())), update, UserVo.class);
        return 0;
    }

    /**
     * @param page
     * @param paramName
     * @param sign
     * @param values
     * @return
     */
    @Override
    public Page<UserVo> findPage(PageRequest page, String[] paramName, final String[] sign, final Object... values) {
        Page<UserVo> pageRes = new Page<>();
        if (values != null && paramName != null && sign != null) {
            if ((paramName.length == sign.length && sign.length == values.length)) {
                Query query = new Query();

                Criteria criteria = new Criteria();

                for (int i = 0; i < paramName.length; i++) {
                    switch (sign[i]) {
                        case OperaSign.eq:
                            criteria.equals(values[i]);
                            query.addCriteria(criteria);
                            break;
                        case OperaSign.gt:
                            criteria.gt(values[i]);
                            query.addCriteria(criteria);
                            break;
                        case OperaSign.gte:
                            criteria.gte(values[i]);
                            query.addCriteria(criteria);
                            break;
                        case OperaSign.lt:
                            criteria.lt(values[i]);
                            query.addCriteria(criteria);
                            break;
                        case OperaSign.lte:
                            criteria.lte(values[i]);
                            query.addCriteria(criteria);
                            break;
                        default:
                            criteria.equals(values[i]);
                            query.addCriteria(criteria);
                            break;
                    }
                }
                long count = mongoTemplate.count(query, UserVo.class);
                List<UserVo> list = mongoTemplate.find(new Query(criteria).with(page).with(page), UserVo.class);
                pageRes.setPageNo(page.getPageNumber());
                pageRes.setPageSize(page.getPageSize());
                pageRes.setTotalCount(count);
                pageRes.setResult(list);
            }
        }
        return pageRes;
    }


}
