package com.herenpeng.rpc;

import com.herenpeng.rpc.bean.Money;
import com.herenpeng.rpc.bean.User;
import com.herenpeng.rpc.client.RpcClient;
import com.herenpeng.rpc.service.UserService;

/**
 * @author herenpeng
 * @since 2021-08-30 22:45
 */
public class MockRpcClient {

    private static final String MockRpcServer = "MockRpcServer";

    public static void main(String[] args) throws InterruptedException {
        // 创建客户端并调用方法
        RpcClient rpcClient = new RpcClient();
        rpcClient.register(MockRpcServer, "127.0.0.1", 10000, MockRpcClient.class);

        Thread.sleep(1500);

        UserService userService = rpcClient.createRpc(MockRpcServer, UserService.class);

        System.out.println(userService.getUsername());

        User user = userService.getUserInfo("肖总");
        System.out.println("同步调用：" + user);

        userService.getUserInfo("肖总", (data) -> {
            System.out.println("异步调用：" + data);
        });


        Money money = rpcClient.invokeMethod(MockRpcServer, "/pay/get", Money.class, 1000L);
        System.out.println("路径式同步调用：" + money);

    }

}
