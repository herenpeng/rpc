package com.herenpeng.rpc;

import com.herenpeng.rpc.client.RpcClient;

import static com.herenpeng.rpc.kit.RpcKit.panic;

/**
 * @author herenpeng
 * @since 2021-08-30 22:45
 */
public class MockRpcClient {

    private static final String MockRpcServer = "MockRpcServer";

    public static void main(String[] args) throws InterruptedException {
        // 创建客户端并调用方法
        RpcClient rpcClient = new RpcClient();
        rpcClient.register(MockRpcServer, "127.0.0.1", 10000);

        Thread.sleep(3000);

        UserService userService = rpcClient.createRpc(MockRpcServer, UserService.class);
        String username = userService.getUsername("肖总");
        System.out.println("同步调用：" + username);

        // userService = rpcClient.createAsyncRpc(MockRpcServer, UserService.class);
        userService.getUsername("肖总", (data, e) -> {
            panic(e);
            System.out.println("异步调用：" + data);
        });
    }

}
