package com.magic.user.vo;

/**
 * FundProfile
 *
 * @author zj
 * @date 2017/5/8
 */
public class FundProfile<T> {

    private String syncTime;//同步时间
    private T info;//详细数据

    public String getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(String syncTime) {
        this.syncTime = syncTime;
    }

    public T getInfo() {
        return info;
    }

    public void setInfo(T info) {
        this.info = info;
    }
}
