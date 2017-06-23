package com.magic.user.entity;

import com.magic.user.enums.ReviewStatus;

/**
 * @Doc 代理审核记录表
 */
public class AgentReview {
    private Long id;

    private Long agentApplyId; //代理申请id

    private String agentName;   //代理账号

    private Long operUserId;    //操作用户id

    private Long ownerId;   //业主id

    private String operUserName;    //操作人账号

    private ReviewStatus status; //审核状态

    private Long createTime;    //操作时间

    private Long agentId;   //代理id，代理申请成功时使用

    public AgentReview() {

    }

    public AgentReview(Long agentApplyId, String agentName, Long operUserId, String operUserName, ReviewStatus status, Long createTime) {
        this.agentApplyId = agentApplyId;
        this.agentName = agentName;
        this.operUserId = operUserId;
        this.operUserName = operUserName;
        this.status = status;
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAgentApplyId() {
        return agentApplyId;
    }

    public void setAgentApplyId(Long agentApplyId) {
        this.agentApplyId = agentApplyId;
    }

    public Long getOperUserId() {
        return operUserId;
    }

    public void setOperUserId(Long operUserId) {
        this.operUserId = operUserId;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getOperUserName() {
        return operUserName;
    }

    public void setOperUserName(String operUserName) {
        this.operUserName = operUserName;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }
}