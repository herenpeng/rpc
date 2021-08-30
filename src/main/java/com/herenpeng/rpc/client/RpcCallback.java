package com.herenpeng.rpc.client;

import com.herenpeng.rpc.exception.RpcException;

/**
 * @author herenpeng
 */
@FunctionalInterface
public interface RpcCallback {

    void run(Object data, RpcException e);

}
