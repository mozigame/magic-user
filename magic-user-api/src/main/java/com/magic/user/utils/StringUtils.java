package com.magic.user.utils;

/**
 * User: joey
 * Date: 2017/5/6
 * Time: 15:55
 */
public class StringUtils {

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    public static String arrayToStrSplit(String[] strings) {
        StringBuffer sb = new StringBuffer();
        for (String s : strings) {
            sb.append(s).append(",");
        }
        String str = "";
        if (sb.length() > 0) {
            str = sb.substring(0, sb.length() - 1);
        }
        return str;
    }

    public static void main(String[] args) {
        String[] strings = new String[]{"aaaa", "bbbb", "cccc"};
        System.out.println(arrayToStrSplit(strings));
    }
}
