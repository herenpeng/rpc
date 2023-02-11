package com.herenpeng.rpc.client;

import com.herenpeng.rpc.config.RpcClientConfig;
import com.herenpeng.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author herenpeng
 */
public class RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private static final Map<String, RpcServerProxy> rpcServerProxyMap = new ConcurrentHashMap<>();

    public void register(String name, String host, int port) {
        register(name, host, port, null);
    }

    public void register(String name, String host, int port, RpcClientConfig clientConfig) {
        RpcServerProxy rpcServerProxy = rpcServerProxyMap.get(name);
        if (rpcServerProxy != null) {
            logger.warn("[RPC客户端]服务{}已注册，请勿重复注册", name);
            return;
        }
        rpcServerProxyMap.put(name, new RpcServerProxy(name, host, port, clientConfig));
        logger.info("[RPC客户端]{}服务注册成功，已注册服务：{}", name, rpcServerProxyMap.keySet());
    }

    // 创建异步 RPC
    // public <T> T createAsyncRpc(String name, Class<?> targetClass) {
    //     return createRpc(name, targetClass, true);
    // }
    //
    // // 创建同步 RPC
    // public <T> T createSyncRpc(String name, Class<?> targetClass) {
    //     return createRpc(name, targetClass, false);
    // }

    /**
     * RPC服务端名称，RPC目标类名称，同步或异步，代理类
     */
    private static final Map<String, Map<String, Object>> rpcMap = new ConcurrentHashMap<>();

    /**
     * 是否异步 async
     */
    public <T> T createRpc(String name, Class<?> targetClass) {
        RpcServerProxy rpcServerProxy = rpcServerProxyMap.get(name);
        if (rpcServerProxy == null) {
            throw new RpcException("[RPC客户端]服务" + name + "未注册，请先注册该服务");
        }
        Map<String, Object> map = rpcMap.computeIfAbsent(name, key -> new ConcurrentHashMap<>());
        // Map<Boolean, Object> rpcProxyMap = map.computeIfAbsent(targetClass.getName(), k -> new ConcurrentHashMap<>());
        Object rpc = map.get(targetClass.getName());
        if (rpc == null) {
            rpc = Proxy.newProxyInstance(targetClass.getClassLoader(), new Class[]{targetClass}, rpcServerProxy);
            map.put(targetClass.getName(), rpc);
        }
        return (T) rpc;
    }


}
