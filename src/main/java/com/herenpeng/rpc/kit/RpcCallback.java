package com.herenpeng.rpc.kit;

/**
 * @author herenpeng
 */
@FunctionalInterface
public interface RpcCallback<T> {

    /**
     * 回调接口执行的方法
     *
     * @param data 回调的返回数据
     */
    void execute(T data);

}
