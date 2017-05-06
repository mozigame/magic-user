package com.magic.user.entity;

import com.magic.user.enums.ReviewStatus;

import java.util.Date;

/**
 * @Doc 代理审核记录表
 */
public class AgentReview {
    private long id;

    private long agentApplyId; //代理申请id

    private long operUserId;    //操作用户id

    private ReviewStatus status; //审核状态

    private Date createTime;    //操作时间

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAgentApplyId() {
        return agentApplyId;
    }

    public void setAgentApplyId(long agentApplyId) {
        this.agentApplyId = agentApplyId;
    }

    public long getOperUserId() {
        return operUserId;
    }

    public void setOperUserId(long operUserId) {
        this.operUserId = operUserId;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}