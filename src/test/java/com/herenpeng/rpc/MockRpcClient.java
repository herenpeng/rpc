package com.herenpeng.rpc;

import com.herenpeng.rpc.bean.Department;
import com.herenpeng.rpc.bean.User;
import com.herenpeng.rpc.client.RpcClient;
import com.herenpeng.rpc.kit.ValueType;
import com.herenpeng.rpc.service.UserService;

import java.util.Arrays;
import java.util.List;

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

        User user = userService.getUserInfo("肖总");
        System.err.println("同步调用1 =====> " + user);

        userService.getUserInfo("肖总", (data) -> {
            System.err.println("异步调用2 =====> " + data);
        });

        List<User> userList = userService.getUserList();
        System.err.println("同步调用3 =====> " + userList);
        for (User u : userList) {
            System.out.println(u.getUsername());
        }
        userService.getUserList((data) -> {
            System.err.println("异步调用4 =====> " + data);
        });

        Department department = rpcClient.get(MockRpcServer, "/department/get", Department.class, "技术部");
        System.err.println("路径式同步调用5 =====> " + department);

        rpcClient.get(MockRpcServer, "/department/get", Department.class, (data) -> {
            System.err.println("路径式异步调用6 =====> " + data);
        }, "技术部");

        Department[] departmentList = rpcClient.get(MockRpcServer, "/department/list", Department[].class);
        System.err.println("路径式同步调用7 =====> ");
        for (Department dept : departmentList) {
            System.err.println(dept.getName());
        }

        rpcClient.get(MockRpcServer, "/department/list", Department[].class, (list) -> {
            System.err.println("路径式异步调用8 =====> " + Arrays.toString(list));
        });


        rpcClient.get(MockRpcServer, "/department/list", new ValueType<List<Department>>() {
        }, (list) -> {
            System.err.println("路径式异步调用9 =====> ");
            for (Department department1 : list) {
                System.out.println(department1.getId() + "---" + department1.getName());
            }
        });

    }

}
