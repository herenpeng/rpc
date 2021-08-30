package com.herenpeng.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author herenpeng
 */
public class RpcServerSyncProxy implements InvocationHandler {

    private final RpcServerProxy rpcServerProxy;

    public RpcServerSyncProxy(RpcServerProxy rpcServerProxy) {
        this.rpcServerProxy = rpcServerProxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // 同步执行
        return rpcServerProxy.invoke(method, args, false);
    }

}
