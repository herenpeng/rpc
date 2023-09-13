package com.herenpeng.rpc.kit;

import com.herenpeng.rpc.annotation.RpcMethod;
import com.herenpeng.rpc.common.RpcMethodLocator;
import com.herenpeng.rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author herenpeng
 */
@Slf4j
public class RpcKit {

    public static void panic(RpcException e) {
        if (e != null) {
            throw e;
        }
    }

    public static RpcMethodLocator getMethodLocator(String className, Method method, String path) {
        RpcMethodLocator methodLocator = getMethodLocator(className, method);
        // 处理路径映射
        RpcMethod rpcMethod = method.getAnnotation(RpcMethod.class);
        if (rpcMethod != null) {
            String subPath = StringUtils.formatPath(rpcMethod.value());
            methodLocator.setPath(path + subPath);
        }
        return methodLocator;
    }


    private static RpcMethodLocator getMethodLocator(String className, Method method) {
        RpcMethodLocator locator = new RpcMethodLocator();
        locator.setClassName(className);
        locator.setMethodName(method.getName());
        Class<?>[] parameterTypes = method.getParameterTypes();
        int length = parameterTypes.length;
        // 是否是异步方法
        locator.setAsync(length > 0 && RpcCallback.class.isAssignableFrom(parameterTypes[length - 1]));
        // 真实参数长度
        length = locator.isAsync() ? length - 1 : length;
        String[] paramTypeNames = new String[length];
        for (int i = 0; i < length; i++) {
            String paramTypeName = parameterTypes[i].getName();
            paramTypeNames[i] = paramTypeName;
        }
        locator.setParamTypeNames(paramTypeNames);
        return locator;
    }


    public static RpcCallback<?> getRpcCallback(Object[] args) {
        if (Collections.isEmpty(args)) {
            return null;
        }
        // 异步
        int callbackIndex = args.length - 1;
        if (RpcCallback.class.isAssignableFrom(args[callbackIndex].getClass())) {
            RpcCallback<?> callback = (RpcCallback<?>) args[callbackIndex];
            args[callbackIndex] = null;
            return callback;
        }
        return null;
    }


    public static Class<?> findApi(List<Class<?>> apiClassList, Class<?> serviceClass) {
        Class<?> api = serviceClass;
        for (Class<?> apiClass : apiClassList) {
            if (apiClass.isAssignableFrom(serviceClass)) {
                api = apiClass;
            }
        }
        return api;
    }


}
