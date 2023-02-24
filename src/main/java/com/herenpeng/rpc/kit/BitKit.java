package com.herenpeng.rpc.kit;

import lombok.extern.slf4j.Slf4j;

/**
 * @author herenpeng
 * @since 2023-02-24 23:49
 */
@Slf4j
public class BitKit {

    public static int setBit(int status, int bit) {
        return status | (1 << bit);
    }


    public static int getBit(int status, int bit) {
        return (status >> bit) & 1;
    }


}
