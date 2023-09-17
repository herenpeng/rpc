package com.herenpeng.rpc.client;

import com.herenpeng.rpc.config.RpcClientConfig;
import com.herenpeng.rpc.exception.RpcException;
import com.herenpeng.rpc.kit.RpcCallback;
import com.herenpeng.rpc.kit.ValueType;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author herenpeng
 */
@Slf4j
public class RpcClient {

    private static final Map<String, RpcServerProxy> rpcServerProxyMap = new ConcurrentHashMap<>();

    public void start(Class<?> rpcApplicationClass, RpcClientConfig clientConfig, List<Class<?>> classList) {
        RpcServerProxy rpcServerProxy = new RpcServerProxy(rpcApplicationClass, clientConfig, classList);
        rpcServerProxyMap.put(rpcServerProxy.getName(), rpcServerProxy);
    }


    public static RpcServerProxy get(String name) {
        RpcServerProxy rpcServerProxy = rpcServerProxyMap.get(name);
        if (rpcServerProxy == null) {
            throw new RpcException("[RPC客户端]服务" + name + "未注册，请先注册该服务");
        }
        return rpcServerProxy;
    }


    /**
     * RPC服务端名称，RPC目标类名称，同步或异步，代理类
     */
    private static final Map<String, Map<String, Object>> rpcMap = new ConcurrentHashMap<>();

    /**
     * 是否异步 async
     */
    public static <T> T createRpc(String name, Class<?> targetClass) {
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

    /**
     * 同步调用方法
     *
     * @param name       服务代理名称
     * @param path       远端路径
     * @param returnType 引用类型
     * @param args       参数
     * @param <T>        泛型
     * @return 同步返回数据
     */
    public static <T> T get(String name, String path, ValueType<T> returnType, Object... args) {
        RpcServerProxy rpcServerProxy = rpcServerProxyMap.get(name);
        if (rpcServerProxy == null) {
            throw new RpcException("[RPC客户端]服务" + name + "未注册，请先注册该服务");
        }
        return rpcServerProxy.invokeMethod(path, args, returnType.get(), false, null);
    }

    /**
     * 同步调用方法
     *
     * @param name       服务代理名称
     * @param path       远端路径
     * @param returnType Class类型
     * @param args       参数
     * @param <T>        泛型
     * @return 同步返回数据
     */
    public static <T> T get(String name, String path, Class<T> returnType, Object... args) {
        RpcServerProxy rpcServerProxy = rpcServerProxyMap.get(name);
        if (rpcServerProxy == null) {
            throw new RpcException("[RPC客户端]服务" + name + "未注册，请先注册该服务");
        }
        return rpcServerProxy.invokeMethod(path, args, returnType, false, null);
    }

    /**
     * 异步调用方法
     *
     * @param name       服务代理名称
     * @param path       远端路径
     * @param returnType 引用类型
     * @param callback   异步回调
     * @param args       参数
     * @param <T>        泛型
     */
    public static <T> void get(String name, String path, ValueType<T> returnType, RpcCallback<T> callback, Object... args) {
        RpcServerProxy rpcServerProxy = rpcServerProxyMap.get(name);
        if (rpcServerProxy == null) {
            throw new RpcException("[RPC客户端]服务" + name + "未注册，请先注册该服务");
        }
        rpcServerProxy.invokeMethod(path, args, returnType.get(), true, callback);
    }

    /**
     * 异步调用方法
     *
     * @param name       服务代理名称
     * @param path       远端路径
     * @param returnType Class类型
     * @param callback   异步回调
     * @param args       参数
     * @param <T>        泛型
     */
    public static <T> void get(String name, String path, Class<T> returnType, RpcCallback<T> callback, Object... args) {
        RpcServerProxy rpcServerProxy = rpcServerProxyMap.get(name);
        if (rpcServerProxy == null) {
            throw new RpcException("[RPC客户端]服务" + name + "未注册，请先注册该服务");
        }
        rpcServerProxy.invokeMethod(path, args, returnType, true, callback);
    }


}
