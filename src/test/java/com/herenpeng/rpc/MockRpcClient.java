package com.herenpeng.rpc;

import com.herenpeng.rpc.bean.Department;
import com.herenpeng.rpc.bean.User;
import com.herenpeng.rpc.client.RpcClient;
import com.herenpeng.rpc.kit.ValueType;
import com.herenpeng.rpc.service.UserService;

import java.util.ArrayList;
import java.util.Date;
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

        // 服务代理调用，返回 User 对象
        rpcServerProxyReturnUser(userService);

        // 服务代理调用，返回 List<User> 对象
        rpcServerProxyReturnUserList(userService);

        // 路径调用，返回 Department 对象
        rpcPathReturnDepartment(rpcClient);

        // 路径调用，返回 Department[] 数组对象
        rpcPathReturnDepartmentArray(rpcClient);

        // 路径调用，返回 List<Department> 集合对象
        rpcPathReturnDepartmentList(rpcClient);

        // 服务代理调用，传递参数是 User 对象
        rpcServerProxyParamUser(userService);

        // 服务代理调用，传递参数是 List<User> 对象
        rpcServerProxyParamUserList(userService);
    }


    private static void rpcServerProxyReturnUser(UserService userService) {
        User user = userService.getUserInfo("肖总");
        System.err.println("同步调用1 =====> " + user);

        userService.getUserInfo("肖总", (data) -> {
            System.err.println("异步调用2 =====> " + data);
        });
    }


    private static void rpcServerProxyReturnUserList(UserService userService) {
        List<User> userList = userService.getUserList();
        for (User user : userList) {
            System.err.println("同步调用3 =====> " + user.getUsername());
        }
        userService.getUserList((data) -> {
            for (User user : data) {
                System.err.println("异步调用4 =====> " + user.getUsername());
            }
        });
    }


    public static void rpcServerProxyParamUser(UserService userService) {
        User user = userService.updateUser(new User(21, "小周", false, 35, new Date(), new Date(), new Date()));
        System.err.println("同步调用10 =====> " + user);
    }


    public static void rpcServerProxyParamUserList(UserService userService) {
        List<User> list = new ArrayList<>();
        list.add(new User(15, "小明", true, 18, new Date(), new Date(), new Date()));
        list.add(new User(16, "小红", false, 21, new Date(), new Date(), new Date()));
        list.add(new User(17, "小雷", true, 25, new Date(), new Date(), new Date()));
        list.add(new User(18, "小刚", true, 29, new Date(), new Date(), new Date()));
        list.add(new User(19, "小李", true, 42, new Date(), new Date(), new Date()));
        list.add(new User(20, "小王", false, 28, new Date(), new Date(), new Date()));
        list.add(new User(21, "小周", false, 35, new Date(), new Date(), new Date()));
        List<User> userList = userService.updateUsers(list);
        for (User user : userList) {
            System.err.println("同步调用11 =====> " + user.getId() + " --- " + user.getUsername());
        }
    }

    private static void rpcPathReturnDepartment(RpcClient rpcClient) {
        Department department = rpcClient.get(MockRpcServer, "/department/get", Department.class, "技术部");
        System.err.println("路径式同步调用5 =====> " + department);

        rpcClient.get(MockRpcServer, "/department/get", Department.class, (data) -> {
            System.err.println("路径式异步调用6 =====> " + data);
        }, "技术部");
    }


    private static void rpcPathReturnDepartmentArray(RpcClient rpcClient) {
        Department[] departmentList = rpcClient.get(MockRpcServer, "/department/list", Department[].class);
        for (Department dept : departmentList) {
            System.err.println("路径式同步调用7 =====> " + dept.getId() + "---" + dept.getName());
        }

        rpcClient.get(MockRpcServer, "/department/list", Department[].class, (list) -> {
            for (Department department : list) {
                System.err.println("路径式异步调用8 =====> " + department.getId() + "---" + department.getName());
            }
        });
    }


    private static void rpcPathReturnDepartmentList(RpcClient rpcClient) {
        rpcClient.get(MockRpcServer, "/department/list", new ValueType<List<Department>>() {
        }, (list) -> {
            for (Department department : list) {
                System.err.println("路径式异步调用9 =====> " + department.getId() + "---" + department.getName());
            }
        });
    }


}
