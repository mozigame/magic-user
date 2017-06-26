package com.magic.user.vo;

import java.io.Serializable;

/**
 * MemberInfoVo
 *
 * @author morton
 * @date 2017/6/10
 */
public class MemberInfoVo implements Serializable{

    private Long stockId;   //股东id
    private Long ownerId;   //业主id
    private Integer level;  //层级

    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
