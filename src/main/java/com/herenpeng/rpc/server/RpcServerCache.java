package com.herenpeng.rpc.server;

import com.herenpeng.rpc.annotation.RpcApi;
import com.herenpeng.rpc.annotation.RpcService;
import com.herenpeng.rpc.common.RpcMethodInvoke;
import com.herenpeng.rpc.common.RpcMethodLocator;
import com.herenpeng.rpc.kit.RpcKit;
import com.herenpeng.rpc.kit.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author herenpeng
 */
@Slf4j
public class RpcServerCache {

    /**
     * cmd生成器
     */
    private static final AtomicInteger cmdGenerator = new AtomicInteger();

    @Getter
    private final Map<Integer, RpcMethodLocator> rpcTable = new ConcurrentHashMap<>();

    private final Map<Integer, RpcMethodInvoke> cmdInvokeMap = new ConcurrentHashMap<>();

    /**
     * 通过方法路径信息，找出对应的方法执行对象
     */
    public RpcMethodInvoke getMethodInvoke(int cmd) {
        return cmdInvokeMap.get(cmd);
    }

    public void initMethodInvoke(List<Class<?>> classList) {
        try {
            List<Class<?>> apiClassList = classList.stream().
                    filter(clazz -> clazz.isInterface() && clazz.getAnnotation(RpcApi.class) != null).collect(Collectors.toList());
            List<Class<?>> serviceClassList = classList.stream().
                    filter(clazz -> !clazz.isInterface() && clazz.getAnnotation(RpcService.class) != null).collect(Collectors.toList());
            // 接口和实现的映射
            for (Class<?> serviceClass : serviceClassList) {
                // 获取注解上的路径
                RpcService service = serviceClass.getAnnotation(RpcService.class);
                String path = StringUtils.formatPath(service.value());
                Class<?> api = RpcKit.findApi(apiClassList, serviceClass);
                Object rpcServer = serviceClass.getDeclaredConstructor().newInstance();
                Method[] methods = api.getDeclaredMethods();
                for (Method method : methods) {
                    RpcMethodInvoke methodInvoke = new RpcMethodInvoke(method, rpcServer);
                    RpcMethodLocator methodLocator = RpcKit.getMethodLocator(api.getName(), method, path);
                    int cmd = cmdGenerator.incrementAndGet();
                    rpcTable.put(cmd, methodLocator);
                    cmdInvokeMap.put(cmd, methodInvoke);
                }
            }
        } catch (Exception e) {
            log.error("[RPC服务端]初始化方法执行数据错误");
            e.printStackTrace();
        }
    }


}
