package com.herenpeng.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author herenpeng
 */
public class RpcServerAsyncProxy implements InvocationHandler {

    private final RpcServerProxy rpcServerProxy;

    public RpcServerAsyncProxy(RpcServerProxy rpcServerProxy) {
        this.rpcServerProxy = rpcServerProxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // 异步执行
        return rpcServerProxy.invoke(method, args, true);
    }

}
