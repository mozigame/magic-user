package com.magic.user.service;

import com.magic.user.entity.User;
import com.magic.user.storage.StockDbService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 17:20
 */
@Service("stockService")
public class StockServiceImpl implements StockService {


    @Resource(name = "stockDbService")
    private StockDbService stockDbService;

    @Override
    public List<Map<String, Object>> findAll() {
        return stockDbService.findAll();
    }

    @Override
    public Map<String, Object> getDetail(long id) {
        return stockDbService.getDetail(id);
    }

    @Override
    public int updatePwd(String pwd, long id) {
        return stockDbService.updatePwd(pwd, id);
    }

    @Override
    public int update(User user) {
        return stockDbService.update(user);
    }

    @Override
    public Long add(User user) {
        return stockDbService.insert(user);
    }

    @Override
    public int disable(long id, int status) {
        return stockDbService.update("updateDisable", new String[]{"id", "status"}, new Object[]{id, status});
    }

    /**
     * @param id
     * @return
     * @Doc 根据股东id获取业主id
     */
    @Override
    public long getOwnerId(long id) {

        return 0;
    }
}
