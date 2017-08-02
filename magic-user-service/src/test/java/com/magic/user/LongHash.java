package com.magic.user;

import com.magic.api.commons.tools.UUIDUtil;
import org.junit.Test;

/**
 * User: joey
 * Date: 2017/5/8
 * Time: 9:54
 */
public class LongHash {

    @Test
    public void getLongHash() {

        System.out.println(UUIDUtil.getSpreadCode());

        System.out.println(0x1008);
        Long a1 = 1023435666123L;
        Long a2 = 1333090332442L;
        Long a3 = 1556758888888L;
        Long a4 = 3344322222222L;
        Long a5 = 8899987788002333L;

        System.out.println(a1.hashCode());
        System.out.println(a2.hashCode());
        System.out.println(a3.hashCode());
        System.out.println(a4.hashCode());
        System.out.println(a5.hashCode());

        System.out.println();
        System.out.println();

        System.out.println(provideHash(a1));
        System.out.println(provideHash(a2));
        System.out.println(provideHash(a3));
        System.out.println(provideHash(a4));
        System.out.println(provideHash(a5));


    }

    private int provideHash(long value) {
        return (int) (value ^ (value >> 32));
    }

    private long provideHashEscap(long value) {
        return  (value / (long) (Math.pow(2, 32)));
    }
}
