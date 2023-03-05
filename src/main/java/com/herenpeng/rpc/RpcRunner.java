package com.herenpeng.rpc;

import com.herenpeng.rpc.annotation.RpcApplication;
import com.herenpeng.rpc.client.RpcClient;
import com.herenpeng.rpc.config.*;
import com.herenpeng.rpc.exception.RpcException;
import com.herenpeng.rpc.kit.ClassScanner;
import com.herenpeng.rpc.kit.StringUtils;
import com.herenpeng.rpc.server.RpcServer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author herenpeng
 * @since 2023-03-05 10:25
 */
@Slf4j
public class RpcRunner {

    public static void run(Class<?> rpcApplicationClass, String[] args) {
        RpcApplication application = readyToStart(rpcApplicationClass);
        // 获取启动类下的所有有效类型字节码对象
        String packageName = rpcApplicationClass.getPackageName();
        ClassScanner scanner = new ClassScanner(packageName);
        List<Class<?>> classList = scanner.listClass();
        // 解析配置文件
        String[] configFiles = application.configFiles();
        // 一份配置默认启动一个 Rpc 实例
        for (String configFile : configFiles) {
            RpcConfig rpcConfig = initRpcConfig(configFile);
            if (checkStartServer(rpcConfig)) {
                // 启动 RPC 服务端
                RpcServer rpcServer = new RpcServer();
                rpcServer.start(rpcApplicationClass, rpcConfig, classList);
            }
            if (checkStartClient(rpcConfig)) {
                // 启动 RPC 客户端
                RpcClient rpcClient = new RpcClient();
                rpcClient.start(rpcApplicationClass, rpcConfig, classList);
            }
        }
    }


    private static RpcConfig initRpcConfig(String configFile) {
        RpcConfigProcessor processor = new RpcConfigProcessor(configFile);
        RpcConfig rpcConfig = processor.getRpc();
        log.info("[RPC启动]配置初始化完成，配置文件名称：{}，配置信息：{}", configFile, rpcConfig);
        return rpcConfig;
    }

    /**
     * 准备启动相关事务
     */
    private static RpcApplication readyToStart(Class<?> rpcApplicationClass) {
        if (rpcApplicationClass == null) {
            throw new RpcException("[RPC启动]RPC启动类对象为空");
        }
        RpcApplication application = rpcApplicationClass.getAnnotation(RpcApplication.class);
        if (application == null) {
            throw new RpcException("[RPC启动]RPC启动类 @RpcApplication 注解为空");
        }
        return application;
    }


    private static boolean checkStartServer(RpcConfig rpcConfig) {
        if (rpcConfig == null || rpcConfig.getServer() == null) {
            return false;
        }
        RpcServerConfig serverConfig = rpcConfig.getServer();
        return serverConfig.getPort() > 0;
    }

    /**
     * 检测是否启动客户端的配置
     *
     * @return 是否启动并注册一个客户端实例
     */
    private static boolean checkStartClient(RpcConfig rpcConfig) {
        if (rpcConfig == null || rpcConfig.getClient() == null) {
            return false;
        }
        RpcClientConfig clientConfig = rpcConfig.getClient();
        return StringUtils.isNotEmpty(clientConfig.getName()) &&
                StringUtils.isNotEmpty(clientConfig.getHost()) &&
                clientConfig.getPort() > 0;
    }


}
