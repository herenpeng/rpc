package com.herenpeng.rpc.server;

import com.herenpeng.rpc.annotation.RpcService;
import com.herenpeng.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author herenpeng
 */
public class RpcServerCache {

    private static final Logger logger = LoggerFactory.getLogger(RpcServerCache.class);

    /**
     * RPC类实例缓存 className -> object
     */
    private final Map<String, Object> rpcServerMap = new ConcurrentHashMap<>();

    private final Map<Class<?>, Class<?>> apiServiceMap = new ConcurrentHashMap<>();

    public void setApiService(Class<?> apiClass, Class<?> serviceClass) {
        apiServiceMap.put(apiClass, serviceClass);
    }

    public Object getRpcServer(String rpcServerClassName) {
        Object rpcServer = rpcServerMap.get(rpcServerClassName);
        try {
            if (rpcServer == null) {
                Class<?> rpcServerClass = Class.forName(rpcServerClassName);
                // 如果指定的这个服务类是接口，尝试查找其实现类型，apiServiceMap的key类型一定是注解了@RpcApi的类型
                if (rpcServerClass.isInterface() && apiServiceMap.containsKey(rpcServerClass)) {
                    rpcServerClass = apiServiceMap.get(rpcServerClass);
                }
                if (rpcServerClass.isAnnotationPresent(RpcService.class)) {
                    rpcServer = rpcServerClass.getDeclaredConstructor().newInstance();
                    rpcServerMap.put(rpcServerClassName, rpcServer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (rpcServer == null) {
            throw new RpcException("[RPC服务端]服务" + rpcServerClassName + "不存在，请检查服务接口及其实现，或检查@RpcService注解");
        }
        return rpcServer;
    }

    /**
     * 字节对象缓存  className -> class
     */
    private final Map<String, Class<?>> paramTypeMap = new ConcurrentHashMap<>();

    public Class<?>[] getParamTypes(String[] paramTypeNames) {
        if (paramTypeNames == null || paramTypeNames.length == 0) {
            return null;
        }
        Class<?>[] paramTypes = new Class[paramTypeNames.length];
        try {
            for (int i = 0; i < paramTypeNames.length; i++) {
                String className = paramTypeNames[i];
                Class<?> cla = paramTypeMap.get(className);
                if (cla == null) {
                    cla = Class.forName(className);
                    paramTypeMap.put(className, cla);
                }
                paramTypes[i] = cla;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paramTypes;
    }

}
