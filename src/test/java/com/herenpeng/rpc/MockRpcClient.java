package com.herenpeng.rpc;

import com.herenpeng.rpc.client.RpcClient;
import com.herenpeng.rpc.kit.thread.RpcScheduler;

/**
 * @author herenpeng
 * @since 2021-08-30 22:45
 */
public class MockRpcClient {

    private static final String MockRpcServer = "MockRpcServer";

    public static void main(String[] args) {
        // 创建客户端并调用方法
        RpcClient rpcClient = new RpcClient();
        rpcClient.register(MockRpcServer, "127.0.0.1", 10000, MockRpcClient.class);

        UserService userService = rpcClient.createRpc(MockRpcServer, UserService.class);

        RpcScheduler.doLoopTask(() -> {
            String username = userService.getUsername("肖总");
            System.out.println("同步调用：" + username);

            userService.getUsername("肖总", (data) -> {
                System.out.println("异步调用：" + data);
            });
        }, 1500, 3000);
    }

}
