package com.herenpeng.rpc.common;

import com.herenpeng.rpc.annotation.RpcApi;
import com.herenpeng.rpc.kit.ClassScanner;
import com.herenpeng.rpc.kit.RpcCallback;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author herenpeng
 * @since 2023-02-11 11:50
 */
public class RpcMethodProcessor {

    private final Map<Method, RpcMethodLocator> methodLocatorMap = new ConcurrentHashMap<>();


    // public Map<Method, RpcMethodLocator> handleClientMethod(Class<?> classObject) {
    //     Map<Method, RpcMethodLocator> map = new HashMap<>();
    //     String packageName = classObject.getPackageName();
    //     ClassScanner scanner = new ClassScanner(packageName, (clazz) -> clazz.getAnnotation(RpcApi.class) != null);
    //     List<Class<?>> classList = scanner.listClass();
    //     for (Class<?> clazz : classList) {
    //         if (clazz.isInterface()) {
    //             handleClass(map, clazz);
    //         }
    //     }
    //     return map;
    // }
    //
    //
    // private void handleClass(Map<Method, RpcMethodLocator> map, Class<?> clazz) {
    //     // 获取接口名称
    //     String className = clazz.getName();
    //     // 获取接口中声明的所有方法
    //     Method[] methods = clazz.getDeclaredMethods();
    //     for (Method method : methods) {
    //         handleMethod(map, className, method);
    //     }
    // }

    // private void handleMethod(String className, Method method) {
    //     RpcMethodLocator locator = new RpcMethodLocator();
    //     locator.setClassName(className);
    //     locator.setMethodName(method.getName());
    //     Class<?>[] parameterTypes = method.getParameterTypes();
    //     int length = parameterTypes.length;
    //     // 是否是异步方法
    //     locator.setAsync(length > 0 && parameterTypes[length - 1].isAssignableFrom(RpcCallback.class));
    //     // 真实参数长度
    //     length = locator.isAsync() ? length - 1 : length;
    //     String[] paramTypeNames = new String[length];
    //     for (int i = 0; i < length; i++) {
    //         String paramTypeName = parameterTypes[i].getName();
    //         paramTypeNames[i] = paramTypeName;
    //     }
    //     locator.setParamTypeNames(paramTypeNames);
    //     // 设置方法和类型的映射关系
    //     map.put(method, locator);
    // }

}
