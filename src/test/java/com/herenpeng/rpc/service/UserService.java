package com.herenpeng.rpc.service;

import com.herenpeng.rpc.annotation.RpcApi;
import com.herenpeng.rpc.bean.User;
import com.herenpeng.rpc.kit.RpcCallback;

import java.util.List;

/**
 * @author herenpeng
 * @since 2021-08-30 23:15
 */
@RpcApi
public interface UserService {

    User getUserInfo(String name);

    /**
     * 这个getUsername方法本质并不会真的执行，只是用来注册回调函数的一个接口方法
     */
    User getUserInfo(String name, RpcCallback<User> callback);


    List<User> getUserList();

    List<User> getUserList(RpcCallback<List<User>> callback);

}
