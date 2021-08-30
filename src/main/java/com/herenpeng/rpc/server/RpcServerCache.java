package com.herenpeng.rpc.server;

import com.herenpeng.rpc.annotation.RpcServerApi;
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

    // RPC类实例缓存 className -> object
    private static final Map<String, Object> rpcServerMap = new ConcurrentHashMap<>();

    public Object getRpcServer(String rpcServerClassName) {
        Object rpcServer = rpcServerMap.get(rpcServerClassName);
        try {
            if (rpcServer == null) {
                Class<?> rpcServerClass = Class.forName(rpcServerClassName);
                if (rpcServerClass.isAnnotationPresent(RpcServerApi.class)) {
                    rpcServer = rpcServerClass.newInstance();
                    rpcServerMap.put(rpcServerClassName, rpcServer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (rpcServer == null) {
            throw new RpcException("[RPC服务端]服务" + rpcServerClassName + "不存在，请检查服务全限定类名，或检查@RpcServerApi注解");
        }
        return rpcServer;
    }

    // 字节对象缓存  className -> class
    private static final Map<String, Class<?>> classMap = new ConcurrentHashMap<>();

    public Class<?>[] getClassList(String[] classNames) {
        if (classNames == null || classNames.length == 0) {
            return null;
        }
        Class<?>[] classList = new Class[classNames.length];
        try {
            for (int i = 0; i < classNames.length; i++) {
                String className = classNames[i];
                Class<?> cla = classMap.get(className);
                if (cla == null) {
                    cla = Class.forName(className);
                    classMap.put(className, cla);
                }
                classList[i] = cla;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classList;
    }

}
