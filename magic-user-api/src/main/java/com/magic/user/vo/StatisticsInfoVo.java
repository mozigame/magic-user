package com.magic.user.vo;

import java.util.Collection;

/**
 * StatisticsInfoVo
 *
 * @author zj
 * @date 2017/6/1
 */
public class StatisticsInfoVo {

    /**
     * x轴数据集
     */
    private Collection<String> dateList;
    /**
     * y轴数据集
     */
    private Collection<Long> activeList;

    public Collection<String> getDateList() {
        return dateList;
    }

    public void setDateList(Collection<String> dateList) {
        this.dateList = dateList;
    }

    public Collection<Long> getActiveList() {
        return activeList;
    }

    public void setActiveList(Collection<Long> activeList) {
        this.activeList = activeList;
    }
}
