package com.magic.user.stockholder.resource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.magic.user.entity.User;
import com.magic.user.enums.AccountStatus;
import com.magic.user.service.StockService;
import com.magic.user.stockholder.resource.service.StockResourceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * User: joey
 * Date: 2017/5/3
 * Time: 15:01
 */
@Service("stockResourceService")
public class StockResourceServiceImpl implements StockResourceService {

    @Resource(name = "stockService")
    private StockService stockService;

    @Override
    public JSONObject findAllStock() {
        List<Map<String, Object>> list = stockService.findAll();
        for (Map<String, Object> map : list) {
            map.put("showStatus", AccountStatus.parse((Integer) map.get("status")).desc());
            map.put("currencyName", AccountStatus.parse((Integer) map.get("currencyType")).desc());
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("list", list);
        return jsonObject;
    }

    @Override
    public JSONObject getStockDetail(long id) {
        Map<String, Object> map = stockService.getDetail(id);
        map.put("showStatus", AccountStatus.parse((Integer) map.get("status")).desc());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("baseInfo", map);
        //TODO 获取档期运营概况
        String operation = "{\n" +
                "            \"syncTime\":\"2017-04-18 09:29:33\",\n" +
                "            \"info\":{\n" +
                "                \"bets\":129000,\n" +
                "                \"notes\":34560000,\n" +
                "                \"betTotalMoney\":\"80500000\",\n" +
                "                \"betEffMoney\":\"78966789\",\n" +
                "                \"gains\":\"5800000\"\n" +
                "            }\n" +
                "        }";
        jsonObject.put("operation", operation);
        return jsonObject;
    }

    @Override
    public String updatePwd(String pwd, long id) {
        int count = stockService.updatePwd(pwd, id);
        return "";
    }

    @Override
    public String update(User user) {
        int count = stockService.update(user);
        return "";
    }

    @Override
    public String add(User user) {
        Long id = stockService.add(user);
        if (id <= 1) {
        }
        JSONObject result = new JSONObject();
        result.put("id", id);
        return result.toJSONString();
    }

    @Override
    public String disable(long id, int status) {
        int count = stockService.disable(id, status);
        return "";
    }
}
