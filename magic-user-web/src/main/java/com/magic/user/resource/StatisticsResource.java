package com.magic.user.resource;

import com.magic.api.commons.core.auth.Access;
import com.magic.api.commons.core.context.RequestContext;
import com.magic.user.resource.service.StatisticsResourceService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * StatisticsResource
 *
 * @author zj
 * @date 2017/5/31
 */
@Controller
@RequestMapping("/v1/user/statistics")
public class StatisticsResource {

    @Resource
    private StatisticsResourceService statisticsResourceService;

    /**
     * 业主/代理指定时间段内活跃会员数 默认当天
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/actives", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String statistics(@RequestParam(name = "startTime", required = false) Long startTime,
                             @RequestParam(name = "endTime", required = false) Long endTime){
        RequestContext rc = RequestContext.getRequestContext();
        return statisticsResourceService.statisMemberLogins(rc, startTime, endTime);
    }

    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/getOwnerAccountLimit", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getOwnerAccountimit(){
        RequestContext rc = RequestContext.getRequestContext();
        return statisticsResourceService.getOwnerAccountLimit(rc);
    }

    @Access(type = Access.AccessType.COMMON)
    @RequestMapping(value = "/getOwnerNotReadNotice", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getOwnerNotReadNotice(){
        RequestContext rc = RequestContext.getRequestContext();

        return statisticsResourceService.getOwnerNotReadNotice(rc.getUid());
    }

}
