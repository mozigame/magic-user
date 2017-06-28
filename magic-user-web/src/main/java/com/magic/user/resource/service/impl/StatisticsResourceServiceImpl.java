package com.magic.user.resource.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.ApiLogger;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.api.commons.model.SimpleListResult;
import com.magic.api.commons.tools.NumberUtil;
import com.magic.bc.query.vo.PrePaySchemeVo;
import com.magic.cms.service.BulletinMsgDubboService;
import com.magic.config.thrift.base.EGResp;
import com.magic.user.constants.UserContants;
import com.magic.user.entity.User;
import com.magic.user.resource.service.StatisticsResourceService;
import com.magic.user.service.StatisticsService;
import com.magic.user.service.UserService;
import com.magic.user.service.dubbo.DubboOutAssembleServiceImpl;
import com.magic.user.service.thrift.ThriftOutAssembleServiceImpl;
import com.magic.user.util.UserUtil;
import com.magic.user.vo.OwnerCreaditLimitVo;
import com.magic.user.vo.StatisticsInfoVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * StatisticsResourceServiceImpl
 *
 * @author zj
 * @date 2017/5/31
 */
@Service
public class StatisticsResourceServiceImpl implements StatisticsResourceService{

    @Resource
    private UserService userService;
    @Resource
    private StatisticsService statisticsService;
    @Resource
    private ThriftOutAssembleServiceImpl thriftOutAssembleService;
    @Resource
    private BulletinMsgDubboService bulletinMsgDubboService;
    @Resource
    private DubboOutAssembleServiceImpl dubboOutAssembleService;
    /**
     * {@inheritDoc}
     */
    @Override
    public String statisMemberLogins(RequestContext rc, Long startTime, Long endTime) {
        long uid = rc.getUid();
        long cmillis = System.currentTimeMillis();
        if (!Optional.ofNullable(startTime).filter(start -> start > 0).isPresent()){
            startTime = cmillis;
        }
        if (!Optional.ofNullable(endTime).filter(end -> end > 0).isPresent()){
            endTime = cmillis;
        }
        if (startTime > endTime){
            return UserContants.EMPTY_STRING;
        }
        User user = userService.get(uid);
        if (!Optional.ofNullable(user).isPresent()){
            return UserContants.EMPTY_STRING;
        }
        Set<String> dates = UserUtil.parseDates(startTime, endTime);
        Map<String, Long> map = statisticsService.getCounts(user, dates);
        return JSON.toJSONString(assembleStatis(map));
    }

    /**
     * 组装返回数据
     * @param map
     * @return
     */
    private StatisticsInfoVo assembleStatis(Map<String, Long> map) {
        StatisticsInfoVo vo = new StatisticsInfoVo();
        vo.setDateList(map.keySet());
        vo.setActiveList(map.values());
        return vo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOwnerAccountLimit(RequestContext rc) {
        long uid = rc.getUid();
//        // -todo 调kaven的thrift接口
//        //EGResp resp = thriftOutAssembleService.getOwnerAccountLimit("{\"uid\":"+uid+"}","account");
//        //String data = resp.data;
//        //String result = "{\"creditLimit\":\"822,121,121\",\"creditLimited\":\"123,1231,12\",\"time\":\""+System.currentTimeMillis()+"\"}";
//        OwnerCreaditLimitVo result = new OwnerCreaditLimitVo();
//
//        Format fm =new DecimalFormat("#,###.00");
//        double d  = (double)((Math.random()*9+1)*3000000000d);
//        result.setCreaditLimit(fm.format(d));
//        result.setCreaditLimited(fm.format(d-120000));
//
////        Calendar cal = Calendar.getInstance();
////        cal.setTime(new Date());
////        cal.add(Calendar.HOUR_OF_DAY,-12);
//        result.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        User user = userService.get(uid);
        //获取授信额度
        PrePaySchemeVo limitr = dubboOutAssembleService.getOwnerLimit(user.getOwnerId());
        Map<String,Object> result = new HashMap<String,Object>();
        if(limitr != null){
            if(limitr.isValid()){
                result.put("type",1);
                result.put("limit",String.valueOf(NumberUtil.fenToYuan(limitr.getBalance())));

                //-TODO 获取要用额度 kaven
                Long limited = 0L;
                result.put("limited",String.valueOf(NumberUtil.fenToYuan(limited)));
            }else{
                result.put("type",0);
            }
        }else{
            result.put("type",0);
        }

        return JSONObject.toJSONString(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOwnerNotReadNotice(Long ownerId) {
        String result = null;
        try {
            result = bulletinMsgDubboService.getMaxNoticeId(ownerId)+"";
        } catch (Exception e) {
            ApiLogger.error("get owner not to read notice failed ! ownerId: %s"+ownerId);
        }
        return result;
    }

}
