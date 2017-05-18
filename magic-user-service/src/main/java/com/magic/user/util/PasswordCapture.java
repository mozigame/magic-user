package com.magic.user.util;

import com.magic.api.commons.core.tools.MD5Util;

/**
 * User: joey
 * Date: 2017/5/18
 * Time: 17:45
 * @Doc 对密码做md5加密
 */
public class PasswordCapture {

    private static final String PRIVATE_KEY = "0INZyBpO";

    public static String getSaltPwd(String password) {
        return MD5Util.md5Digest((PRIVATE_KEY + password).getBytes());
    }
}
