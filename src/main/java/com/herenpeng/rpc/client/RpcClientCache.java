package com.herenpeng.rpc.client;

import com.herenpeng.rpc.common.RpcMethodLocator;
import com.herenpeng.rpc.kit.RpcKit;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author herenpeng
 * @since 2023-02-11 21:58
 */
@Slf4j
public class RpcClientCache {

    private final Map<Method, RpcMethodLocator> methodLocatorMap = new ConcurrentHashMap<>();

    public RpcMethodLocator getMethodLocator(Method method) {
        RpcMethodLocator locator = methodLocatorMap.get(method);
        if (locator == null) {
            Class<?> classObject = method.getDeclaringClass();
            String className = classObject.getName();
            locator = RpcKit.getMethodLocator(className, method);
            methodLocatorMap.put(method, locator);
        }
        return locator;
    }


}
