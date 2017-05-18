package com.magic.user.test;

import com.alibaba.fastjson.JSONObject;
import com.magic.user.entity.AgentReview;
import com.magic.user.enums.ReviewStatus;
import org.junit.Test;

/**
 * User: joey
 * Date: 2017/5/17
 * Time: 18:09
 */
public class MyTest {

    @Test
    public void reviewTest() {
        AgentReview review = new AgentReview();
        review.setAgentApplyId(1000L);
        review.setAgentName("bbbb");
        review.setOperUserId(100003L);
        review.setOperUserName("fdssd");
        review.setStatus(ReviewStatus.noReview);
        review.setCreateTime(System.currentTimeMillis());

        String reviewStr = JSONObject.toJSONString(review);
        System.out.println(reviewStr);
        AgentReview review1 = JSONObject.parseObject(reviewStr, AgentReview.class);
        System.out.println(review1.getStatus().value());
    }
}
