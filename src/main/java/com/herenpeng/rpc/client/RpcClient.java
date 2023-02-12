package com.herenpeng.rpc.client;

import com.herenpeng.rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author herenpeng
 */
@Slf4j
public class RpcClient {

    private final Map<String, RpcServerProxy> rpcServerProxyMap = new ConcurrentHashMap<>();

    public void register(String name, String host, int port, Class<?> rpcScannerClass) {
        RpcServerProxy rpcServerProxy = rpcServerProxyMap.get(name);
        if (rpcServerProxy != null) {
            log.warn("[RPC客户端]服务{}已注册，请勿重复注册", name);
            return;
        }
        rpcServerProxyMap.put(name, new RpcServerProxy(name, host, port, rpcScannerClass));
        log.info("[RPC客户端]{}服务注册成功，已注册服务：{}", name, rpcServerProxyMap.keySet());
    }

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
        Object rpc = map.get(targetClass.getName());
        if (rpc == null) {
            rpc = Proxy.newProxyInstance(targetClass.getClassLoader(), new Class[]{targetClass}, rpcServerProxy);
            map.put(targetClass.getName(), rpc);
        }
        return (T) rpc;
    }


}
