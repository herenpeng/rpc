package com.herenpeng.rpc;

import com.herenpeng.rpc.client.RpcClient;
import com.herenpeng.rpc.server.RpcServer;

/**
 * @author herenpeng
 * @since 2021-08-30 22:45
 */
public class RpcTest {

    public static void main(String[] args) {
        // 创建客户端并调用方法
        RpcClient rpcClient = new RpcClient();
        rpcClient.register("RpcServer", "127.0.0.1", 10000);
        UserService userService = rpcClient.createSyncRpc("RpcServer", UserService.class);
        String username = userService.getUsername("肖总");
        System.out.println(username);

        // 创建服务端
        RpcServer rpcServer = new RpcServer();
        rpcServer.start(10000);
    }

}
