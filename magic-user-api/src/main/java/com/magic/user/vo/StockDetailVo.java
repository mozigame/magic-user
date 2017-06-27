package com.magic.user.vo;

/**
 * MemberDetailVo
 *
 * @author zj
 * @date 2017/5/8
 */
public class StockDetailVo {

    /**
     * 股东基础信息
     */
    private StockInfoVo baseInfo;
    /**
     * 会员资金概况
     */
    private FundProfile<StockFundInfo> operation;

    public StockInfoVo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(StockInfoVo baseInfo) {
        this.baseInfo = baseInfo;
    }

    public FundProfile<StockFundInfo> getOperation() {
        return operation;
    }

    public void setOperation(FundProfile<StockFundInfo> operation) {
        this.operation = operation;
    }
}
