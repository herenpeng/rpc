package com.herenpeng.rpc;

import com.herenpeng.rpc.annotation.RpcClientApi;
import com.herenpeng.rpc.client.RpcCallback;

/**
 * @author herenpeng
 * @since 2021-08-30 23:15
 */
@RpcClientApi
public interface UserService {

    void setUsername(String name);

    String getUsername(String name);

    String getUsername(String name, RpcCallback callback);

}
