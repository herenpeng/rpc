package com.herenpeng.rpc.protocol;

import org.junit.Test;

/**
 * @author herenpeng
 * @since 2023-02-21 23:06
 */
public class ProtocolTest {

    @Test
    public void test01() {
        byte b = (byte) ((1 << 7) | 1);
        System.out.println(Integer.toBinaryString(b));
        System.out.println(b);

        System.out.println(b & 0X7F);

    }

}
