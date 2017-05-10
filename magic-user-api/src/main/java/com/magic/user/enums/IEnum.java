package com.magic.user.enums;

/**
 * User: joey
 * Date: 2017/5/9
 * Time: 20:21
 */
public interface IEnum<E extends Enum<E>> {
    int value();

    String desc();

}
