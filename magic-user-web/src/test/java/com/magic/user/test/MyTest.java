package com.magic.user.test;

import com.alibaba.fastjson.JSONObject;
import com.magic.api.commons.tools.NumberUtil;
import com.magic.user.entity.AgentReview;
import com.magic.user.enums.ReviewStatus;
import org.junit.Test;

import java.util.*;

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

    @Test
    public void mapJsonTest() {

        JSONObject j1 = new JSONObject();
        Collection<Long> ids= new HashSet<>();
        ids.add(11111L);
        ids.add(22222L);
        ids.add(33333L);
        j1.put("memberIds", ids);
        System.out.println(j1.toJSONString());


        JSONObject object = new JSONObject();
        Map<Integer, JSONObject> map = new HashMap<>();
        JSONObject mapJ = new JSONObject();
        mapJ.put("returnWater",1);
        mapJ.put("returnWaterName","返水基本1");

        JSONObject mapJ1 = new JSONObject();
        mapJ1.put("returnWater",2);
        mapJ1.put("returnWaterName","返水基本2");

        map.put(1, mapJ);
        map.put(2, mapJ1);
        object.put("data", map);
        System.out.println(object.toJSONString());

        System.out.println(JSONObject.parseObject(object.toJSONString()));
    }

    public static void main(String args[]){
        System.out.println(NumberUtil.fenToYuan(370384874)) ;
    }
}
