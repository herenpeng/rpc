package com.herenpeng.rpc.client;

import com.herenpeng.rpc.annotation.RpcApi;
import com.herenpeng.rpc.annotation.RpcService;
import com.herenpeng.rpc.common.RpcMethodLocator;
import com.herenpeng.rpc.kit.RpcKit;
import com.herenpeng.rpc.kit.StringUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author herenpeng
 * @since 2023-02-11 21:58
 */
@Slf4j
public class RpcClientCache {

    private final Map<Method, Integer> methodCmdMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> pathCmdMap = new ConcurrentHashMap<>();

    @Setter
    private List<Class<?>> classList;

    private Map<Integer, RpcMethodLocator> rpcTable;


    private Map<RpcMethodLocator, Integer> reverseMap() {
        Map<RpcMethodLocator, Integer> locatorCmdMap = new HashMap<>();
        for (Map.Entry<Integer, RpcMethodLocator> entry : rpcTable.entrySet()) {
            locatorCmdMap.put(entry.getValue(), entry.getKey());
        }
        return locatorCmdMap;
    }

    /**
     * 初始化方法定位符
     */
    public void initMethodCmd(Map<Integer, RpcMethodLocator> rpcTable) {
        this.rpcTable = rpcTable;
        Map<RpcMethodLocator, Integer> locatorCmdMap = reverseMap();
        try {
            List<Class<?>> apiClassList = classList.stream()
                    .filter(clazz -> clazz.isInterface() && clazz.getAnnotation(RpcApi.class) != null).collect(Collectors.toList());
            List<Class<?>> serviceClassList = classList.stream()
                    .filter(clazz -> !clazz.isInterface() && clazz.getAnnotation(RpcService.class) != null).collect(Collectors.toList());
            // 接口和实现的映射
            for (Class<?> serviceClass : serviceClassList) {
                // 获取注解上的路径
                RpcService service = serviceClass.getAnnotation(RpcService.class);
                String path = StringUtils.formatPath(service.value());
                Class<?> api = RpcKit.findApi(apiClassList, serviceClass);
                Method[] methods = api.getDeclaredMethods();
                for (Method method : methods) {
                    RpcMethodLocator methodLocator = RpcKit.getMethodLocator(api.getName(), method, path);
                    Integer cmd = locatorCmdMap.get(methodLocator);
                    if (cmd == null) {
                        continue;
                    }
                    methodCmdMap.put(method, cmd);
                    if (StringUtils.isNotEmpty(methodLocator.getPath())) {
                        pathCmdMap.put(methodLocator.getPath(), cmd);
                    }
                }
            }
        } catch (Exception e) {
            log.error("[RPC客户端端]初始化RPC表错误");
            e.printStackTrace();
        }
    }


    public RpcMethodLocator getMethodLocator(int cmd) {
        return rpcTable.get(cmd);
    }


    public int getCmd(Method method) {
        return methodCmdMap.getOrDefault(method, 0);
    }

    public int getCmd(String path) {
        return pathCmdMap.getOrDefault(path, 0);
    }


}
