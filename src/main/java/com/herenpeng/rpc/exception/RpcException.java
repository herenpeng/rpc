package com.herenpeng.rpc.exception;

/**
 * @author herenpeng
 */
public class RpcException extends RuntimeException {

    public RpcException() {
        super();
    }

    public RpcException(String message) {
        super(message);
    }

}
