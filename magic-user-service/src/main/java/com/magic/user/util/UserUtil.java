package com.magic.user.util;

import com.magic.api.commons.tools.CommonDateParseUtil;
import com.magic.api.commons.tools.LocalDateTimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * UserUtil
 *
 * @author zj
 * @date 2017/5/31
 */
public class UserUtil {

    private static String [] names = {
            "admin","administrator","管理员","版主", "超级版主","超级管理员","本站管理员","本版版主",
            "会员","超级会员","vip",".com", ".cn","用户管理","管理员"
    };

    /**
     * 解析区间段，按天
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static Set<String> parseDates(Long startTime, Long endTime) {
        LocalDate start = LocalDateTimeUtil.toLocalDate(startTime);
        LocalDate end = LocalDateTimeUtil.toLocalDate(endTime);

        Set<String> result = new LinkedHashSet<>();
        do{
            result.add(start.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            start = start.plusDays(1);
        }while (start.isBefore(end));
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

    /**
     * 按照一定的规则产生验证码
     *
     * @return
     */
    public static String checkCode() {
        // 声明返回值
        String temp = "";
        // 使用随机生成器对象
        Random rd = new Random();
        // 验证码位数 4位
        for (int i = 0; i < 4; i++) {
            // 每一位 产生字母的规则的 随机数
            int m = rd.nextInt(3); // 0 --1 2
            // 每一位规则的生成器
            switch (m) {
                case 0: // 规则 a-z 65--90 25;
                    char c1 = (char) (rd.nextInt(26) + 65);
                    temp += c1;
                    break;
                case 1: // 规则 A-Z 97--122
                    char c2 = (char) (rd.nextInt(26) + 97);
                    temp += c2;
                    break;
                case 2: // 0--9;
                    int num = rd.nextInt(10);
                    temp += num;
                    break;
            }
        }
        // 返回
        return temp;
    }

    /**
     * 校验字符串是否包非法值
     * @param name
     * @return
     */
    public static boolean checkoutUserName(String name){
        for (String str : names) {
            if(name.indexOf(str) != -1){
                return true;
            }
        }
        return false;
    }

}
