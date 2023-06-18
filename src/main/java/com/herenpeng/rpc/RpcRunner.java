package com.herenpeng.rpc;

import com.herenpeng.rpc.annotation.RpcApplication;
import com.herenpeng.rpc.client.RpcClient;
import com.herenpeng.rpc.config.*;
import com.herenpeng.rpc.exception.RpcException;
import com.herenpeng.rpc.kit.ClassScanner;
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
        String configFile = application.value();
        // 一份配置默认启动一个 Rpc 实例
        RpcConfig rpcConfig = initRpcConfig(configFile);
        rpcServerStart(rpcApplicationClass, classList, rpcConfig);
        rpcClientStart(rpcApplicationClass, classList, rpcConfig);
    }

    private static void rpcServerStart(Class<?> rpcApplicationClass, List<Class<?>> classList, RpcConfig rpcConfig) {
        RpcServerConfig server = rpcConfig.getServer();
        if (server != null) {
            RpcServer rpcServer = new RpcServer();
            rpcServer.start(rpcApplicationClass, server, classList);
        }
        List<RpcServerConfig> serverConfigs = rpcConfig.getServers();
        if (serverConfigs != null) {
            // 启动 RPC 服务端
            for (RpcServerConfig serverConfig : serverConfigs) {
                RpcServer rpcServer = new RpcServer();
                rpcServer.start(rpcApplicationClass, serverConfig, classList);
            }
        }
    }


    private static void rpcClientStart(Class<?> rpcApplicationClass, List<Class<?>> classList, RpcConfig rpcConfig) {
        RpcClientConfig client = rpcConfig.getClient();
        if (client != null) {
            RpcClient rpcClient = new RpcClient();
            rpcClient.start(rpcApplicationClass, client, classList);
        }
        List<RpcClientConfig> clientConfigs = rpcConfig.getClients();
        if (clientConfigs != null) {
            // 启动 RPC 客户端
            for (RpcClientConfig clientConfig : clientConfigs) {
                RpcClient rpcClient = new RpcClient();
                rpcClient.start(rpcApplicationClass, clientConfig, classList);
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


}
