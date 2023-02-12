package com.herenpeng.rpc.kit;

import com.herenpeng.rpc.common.RpcMethodLocator;
import com.herenpeng.rpc.exception.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

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


    public static RpcMethodLocator getMethodLocator(String className, Method method) {
        RpcMethodLocator locator = new RpcMethodLocator();
        locator.setClassName(className);
        locator.setMethodName(method.getName());
        Class<?>[] parameterTypes = method.getParameterTypes();
        int length = parameterTypes.length;
        // 是否是异步方法
        locator.setAsync(length > 0 && parameterTypes[length - 1].isAssignableFrom(RpcCallback.class));
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


    public static Object[] getMethodParams(Object[] args, boolean async) {
        if (!async) {
            return args;
        }
        // 异步
        int length = args.length - 1;
        Object[] params = new Object[length];
        System.arraycopy(args, 0, params, 0, length);
        return params;
    }


}
