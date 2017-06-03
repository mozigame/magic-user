package com.magic.user.util;

import com.magic.api.commons.tools.CommonDateParseUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * UserUtil
 *
 * @author zj
 * @date 2017/5/31
 */
public class UserUtil {

    /**
     * 解析区间段，按天
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static Set<String> parseDates(Long startTime, Long endTime) {
        Calendar start = Calendar.getInstance();
        start.setTime(parseLong(startTime));
        Calendar end = Calendar.getInstance();
        end.setTime(parseLong(endTime));
        Set<String> result = new LinkedHashSet<>();
        do{
            result.add(CommonDateParseUtil.date2string(start.getTime(), CommonDateParseUtil.YYYYMMDD));
            start.add(Calendar.DAY_OF_YEAR, 1);
        }while (start.compareTo(end) <= 0);
        return result;
    }

    /**
     * 13位时间戳只保留年月日
     *
     * @param time
     * @return
     */
    public static Date parseLong(Long time) {
        String dateStr = CommonDateParseUtil.longToStringDate(time, CommonDateParseUtil.YYYYMMDD);
        return CommonDateParseUtil.string2date(dateStr, CommonDateParseUtil.YYYYMMDD);
    }

}
