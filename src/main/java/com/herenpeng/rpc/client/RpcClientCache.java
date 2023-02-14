package com.herenpeng.rpc.client;

import com.herenpeng.rpc.common.RpcMethodLocator;
import com.herenpeng.rpc.kit.RpcKit;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author herenpeng
 * @since 2023-02-11 21:58
 */
@Slf4j
public class RpcClientCache {

    private final Map<Method, RpcMethodLocator> methodLocatorMap = new ConcurrentHashMap<>();

    /**
     * 初始化方法定位符
     */
    public void initMethodLocator(List<Class<?>> classList) {
        for (Class<?> clazz : classList) {
            if (!clazz.isInterface()) {
                // 必须是接口
                continue;
            }
            // 获取接口名称
            String className = clazz.getName();
            // 获取接口中声明的所有方法
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                RpcMethodLocator locator = RpcKit.getMethodLocator(className, method);
                methodLocatorMap.put(method, locator);
            }
        }
    }


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
