package com.magic.user.pwdtest;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.magic.user.util.PasswordCapture;
import org.junit.Test;
import org.springframework.web.servlet.tags.form.PasswordInputTag;

import java.util.ArrayList;
import java.util.List;

/**
 * User: joey
 * Date: 2017/5/20
 * Time: 14:30
 */
public class UserPasswordTest {

    @Test
    public void testPwd() {
        System.out.println(PasswordCapture.getSaltPwd("55555"));
    }

    @Test
    public void testNumJson() {
        JSONObject jsonObject = new JSONObject();
        List<String> strs = new ArrayList<>();
        strs.add("11111");
        strs.add("3242");
        strs.add("431432");
        jsonObject.put("电子游戏",strs);
        System.out.println(jsonObject.toJSONString());
        System.out.println(jsonObject.getString("电子游戏"));
        System.out.println(JSONObject.parseObject(jsonObject.getString("电子游戏"), List.class));
        System.out.println();

    }
}
