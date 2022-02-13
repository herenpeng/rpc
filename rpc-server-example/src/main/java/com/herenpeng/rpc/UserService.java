package com.herenpeng.rpc;

import com.herenpeng.rpc.annotation.RpcServerApi;

/**
 * @author herenpeng
 * @since 2021-08-30 23:05
 */
@RpcServerApi
public class UserService {

    public void setUsername(String name) {
        System.out.println("Hello,World:" + name);
    }

    public String getUsername(String name) {
        return "RPC调用，调用参数：" + name + "，响应回调时间：" + System.currentTimeMillis();
    }
}
