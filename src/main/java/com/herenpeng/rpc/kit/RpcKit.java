package com.herenpeng.rpc.kit;

import com.herenpeng.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author herenpeng
 */
public class RpcKit {

    private static final Logger logger = LoggerFactory.getLogger(RpcKit.class);

    public static void panic(RpcException e) {
        if (e != null) {
            throw e;
        }
    }

}
