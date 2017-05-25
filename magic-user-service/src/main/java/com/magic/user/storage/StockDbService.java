package com.magic.user.storage;

import com.magic.api.commons.atlas.core.BaseDao;
import com.magic.user.dao.LoginDao;
import com.magic.user.dao.StockDao;
import com.magic.user.entity.User;
import com.magic.user.storage.base.BaseDbServiceImpl;
import com.magic.user.vo.StockInfoVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 17:10
 */
@Service("stockDbService")
public class StockDbService extends BaseDbServiceImpl<User, Long> {

    @Resource
    private StockDao stockDao;
    @Resource
    private LoginDao loginDao;

    @Override
    public BaseDao<User, Long> getDao() {
        return stockDao;
    }

    /**
     * @return
     * @Doc 查询所有股东信息
     */
    public List<StockInfoVo> findAll(Long ownerId) {
        try {
            return stockDao.findCustom("findAllStock", new String[]{"ownerId"}, new Object[]{ownerId});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param id
     * @return
     * @Doc 根据id查询股东详细信息
     */
    public StockInfoVo getDetail(Long id) {
        try {
            return (StockInfoVo) stockDao.get("getStockDetail", new String[]{"id"}, new Object[]{id});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
