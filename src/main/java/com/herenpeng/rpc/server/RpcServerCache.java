package com.herenpeng.rpc.server;

import com.herenpeng.rpc.annotation.RpcApi;
import com.herenpeng.rpc.annotation.RpcService;
import com.herenpeng.rpc.common.RpcMethodInvoke;
import com.herenpeng.rpc.common.RpcMethodLocator;
import com.herenpeng.rpc.kit.RpcKit;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author herenpeng
 */
@Slf4j
public class RpcServerCache {

    private final Map<RpcMethodLocator, RpcMethodInvoke> methodInvokeMap = new ConcurrentHashMap<>();

    public RpcMethodInvoke getMethodInvoke(RpcMethodLocator methodLocator) {
        return methodInvokeMap.get(methodLocator);
    }

    public void initMethodInvoke(List<Class<?>> classList) {
        try {
            List<Class<?>> apiClassList = new ArrayList<>();
            List<Class<?>> serviceClassList = new ArrayList<>();
            classList.forEach((clazz) -> {
                if (clazz.isInterface() && clazz.getAnnotation(RpcApi.class) != null) {
                    apiClassList.add(clazz);
                }
                if (!clazz.isInterface() && clazz.getAnnotation(RpcService.class) != null) {
                    serviceClassList.add(clazz);
                }
            });
            // 接口和实现的映射
            for (Class<?> serviceClass : serviceClassList) {
                Class<?> api = serviceClass;
                for (Class<?> apiClass : apiClassList) {
                    if (apiClass.isAssignableFrom(serviceClass)) {
                        api = apiClass;
                    }
                }
                Object rpcServer = serviceClass.getDeclaredConstructor().newInstance();
                Method[] methods = api.getDeclaredMethods();
                for (Method method : methods) {
                    RpcMethodInvoke methodInvoke = new RpcMethodInvoke(method, rpcServer);
                    RpcMethodLocator methodLocator = RpcKit.getMethodLocator(api.getName(), method);
                    methodInvokeMap.put(methodLocator, methodInvoke);
                }
            }
        } catch (Exception e) {
            log.error("[RPC服务端]初始化方法执行数据错误");
            e.printStackTrace();
        }
    }


}
