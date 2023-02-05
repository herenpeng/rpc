package com.herenpeng.rpc;

import com.herenpeng.rpc.exception.RpcException;

/**
 * @author herenpeng
 */
@FunctionalInterface
public interface RpcCallback<T> {

    /**
     * 回调接口执行的方法
     *
     * @param data 回调的返回数据
     * @param e    返回的异常信息
     */
    void response(T data, RpcException e);

}
