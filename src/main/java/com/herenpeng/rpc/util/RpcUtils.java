package com.herenpeng.rpc.util;

import com.herenpeng.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author herenpeng
 */
public class RpcUtils {

    private static final Logger logger = LoggerFactory.getLogger(RpcUtils.class);


    public static void panic(RpcException e) {
        if (e != null) {
            throw e;
        }
    }

}
