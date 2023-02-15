package com.herenpeng.rpc;

import com.herenpeng.rpc.annotation.RpcService;
import com.herenpeng.rpc.kit.RpcCallback;

/**
 * @author herenpeng
 * @since 2021-08-30 23:05
 */
@RpcService
public class UserServiceImpl implements UserService {

    @Override
    public void setUsername(String name) {
        System.out.println("Hello,World:" + name);
    }

    @Override
    public String getUsername(String name) {
        return "RPC调用，调用参数：" + name + "，响应回调时间：" + System.currentTimeMillis();
    }

    @Override
    public String getUsername(String name, RpcCallback<String> callback) {
        return getUsername(name);
    }

}
