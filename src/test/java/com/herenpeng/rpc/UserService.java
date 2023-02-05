package com.herenpeng.rpc;

import com.herenpeng.rpc.annotation.RpcApi;

/**
 * @author herenpeng
 * @since 2021-08-30 23:15
 */
@RpcApi
public interface UserService {

    void setUsername(String name);

    String getUsername(String name);

    /**
     * 这个getUsername方法本质并不会真的执行，只是用来注册回调函数的一个接口方法
     */
    String getUsername(String name, RpcCallback<String> callback);

}
