package com.herenpeng.rpc.service;

import com.herenpeng.rpc.annotation.RpcService;
import com.herenpeng.rpc.bean.User;
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
    public String getUsername() {
        return "RPC的远程调用";
    }

    @Override
    public User getUserInfo(String name) {
        return new User(15, name, true, 18, System.currentTimeMillis());
    }

    @Override
    public User getUserInfo(String name, RpcCallback<User> callback) {
        return getUserInfo(name);
    }

}
