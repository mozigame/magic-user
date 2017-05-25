package com.magic.user.pwdtest;

import com.magic.user.util.PasswordCapture;
import org.junit.Test;
import org.springframework.web.servlet.tags.form.PasswordInputTag;

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
}
