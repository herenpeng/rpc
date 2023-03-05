# RPC 框架

项目地址：https://github.com/herenpeng/rpc.git

- 基于 JDK11 开发
- 该项目是基于 Netty4 的一个简易的 RPC 框架
- 配置即实例，无需编写代码，根据配置内容自动实例化 RPC 实例
- 拥有完备的服务启动、心跳监控、服务重连等机制
- 客户端支持动态代理，路径调用两种远程调用方式
- 支持同步和异步调用，异步支持函数式回调
- 支持 Json 和 Hessian 两种序列化方式，可以通过配置自由切换，无需改动代码
- 支持自定义通信协议

<p align="center">
	<a target="_blank" href="https://www.oracle.com/java/technologies/downloads/#java11">
		<img src="https://img.shields.io/badge/JDK-11-green.svg" alt="JDK 11" />
	</a>
	<br />
	<a target="_blank" href='https://gitee.com/herenpeng/rpc'>
		<img src='https://gitee.com/herenpeng/rpc/badge/star.svg' alt='gitee star'/>
	</a>
	<a target="_blank" href='https://github.com/herenpeng/rpc'>
		<img src="https://img.shields.io/github/stars/herenpeng/rpc.svg?logo=github" alt="github star"/>
	</a>
</p>
<hr/>

<br/>


## RPC 入门

RPC 框架的入门，首先要使用这个 RPC 框架，搭建一个简单的 RPC 环境，使得 RPC 客户端可以通过 RPC 协议远程调用 RPC 服务端。

1、从 GitHub 上下载项目，项目地址：`https://github.com/herenpeng/rpc.git`

2、使用 Maven 将项目安装到本地仓库中。

3、搭建一个简单的 Maven 工程。

在项目中，引入 rpc 框架的依赖：

```xml
<dependencies>
    <!--rpc框架依赖-->
    <dependency>
        <groupId>com.herenpeng</groupId>
        <artifactId>rpc</artifactId>
        <version>1.0.0</version>
    </dependency>
    <!--rpc框架中只引入了slf4j的接口依赖，框架实现需要自己引入-->
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.2.3</version>
    </dependency>
</dependencies>
```

## 配置即实例

一份完整的配置，即是一个完整的 RPC 对象，如果需要启动一个 RPC 客户端，可以使用以下的配置：

```yaml
rpc:
  client:
    name: MockRpcClient
    host: 127.0.0.1
    port: 10000
    sync-timeout: 1000
    reconnection-time: 1000
    heartbeat-time: 10000
    heartbeat-invalid-times: 3
    serialize: 1
    heartbeat-log-enable: false
    monitor-log-enable: true
```

如果需要启动一个 RPC 服务端，可以使用以下的配置：
```yaml
rpc:
  server:
    name: MockRpcServer
    port: 10000
    heartbeat-log-enable: false
```

- 如果需要同时启动一个 RPC 服务端和一个 RPC 客户端，那么只需要将两份配置合并即可。

- 如果需要启动多个 RPC 服务端或者多个 RPC 客户端，则可以创建多份配置文件，并通过注解 `@RpcApplication` 中的属性 `configFiles()` 来标识多份需要实例化的配置文件。

## 初始化RPC实例

> 在项目下创建`com.herenpeng.rpc`文件包，并在该包下创建`MockRpc.java`文件。

RPC 框架，可以直接使用`RpcRunner`启动类根据配置文件信息来初始化 RPC 实例：

```java
@RpcApplication
public class MockRpc {

    public static void main(String[] args) throws Exception  {
        RpcRunner.run(MockRpc.class, args);
    }

}
```

## RPC 服务代理调用

### 创建服务端接口

> 在项目`com.herenpeng.rpc`文件包下创建`UserService`接口和`UserServiceImpl`实现类。

```java
@RpcApi
public interface UserService {

    User getUserInfo(String name);

    User getUserInfo(String name, RpcCallback<User> callback);

    List<User> getUserList();

    List<User> getUserList(RpcCallback<List<User>> callback);

    User updateUser(User user);

    List<User> updateUsers(List<User> users);
}
```

```java
@RpcService
public class UserServiceImpl implements UserService {

    @Override
    public User getUserInfo(String name) {
        return new User(15, name, true, 18, new Date(), new Date(), new Date());
    }

    @Override
    public User getUserInfo(String name, RpcCallback<User> callback) {
        return getUserInfo(name);
    }

    @Override
    public List<User> getUserList() {
        List<User> list = new ArrayList<>();
        list.add(new User(15, "小明", true, 18, new Date(), new Date(), new Date()));
        list.add(new User(16, "小红", false, 21, new Date(), new Date(), new Date()));
        list.add(new User(17, "小雷", true, 25, new Date(), new Date(), new Date()));
        list.add(new User(18, "小刚", true, 29, new Date(), new Date(), new Date()));
        list.add(new User(19, "小李", true, 42, new Date(), new Date(), new Date()));
        list.add(new User(20, "小王", false, 28, new Date(), new Date(), new Date()));
        list.add(new User(21, "小周", false, 35, new Date(), new Date(), new Date()));
        return list;
    }

    @Override
    public List<User> getUserList(RpcCallback<List<User>> callback) {
        return getUserList();
    }

    @Override
    public User updateUser(User user) {
        user.setUsername("修改后的用户名");
        return user;
    }

    @Override
    public List<User> updateUsers(List<User> users) {
        List<User> list = new ArrayList<>();
        list.add(new User(15, "小明2", true, 18, new Date(), new Date(), new Date()));
        list.add(new User(16, "小红2", false, 21, new Date(), new Date(), new Date()));
        list.add(new User(17, "小雷2", true, 25, new Date(), new Date(), new Date()));
        list.add(new User(18, "小刚3", true, 29, new Date(), new Date(), new Date()));
        list.add(new User(19, "小李4", true, 42, new Date(), new Date(), new Date()));
        list.add(new User(20, "小王5", false, 28, new Date(), new Date(), new Date()));
        list.add(new User(21, "小周6", false, 35, new Date(), new Date(), new Date()));
        return list;
    }
}
```

### 创建客户端接口

> 在项目`com.herenpeng.rpc`文件包下创建 `UserService` 接口，`UserService`可以和服务端使用同一个接口文件。

### RPC客户端调用

> 启动`MockRpc`类。

```java
@RpcApplication
public class MockRpc {

    private static final String MockRpcClient = "MockRpcClient";

    public static void main(String[] args) throws Exception {
        RpcRunner.run(MockRpc.class, args);
        
        Thread.sleep(1500);

        UserService userService = RpcClient.createRpc(MockRpcClient, UserService.class);

        // 服务代理调用，返回 User 对象
        rpcServerProxyReturnUser(userService);

        // 服务代理调用，返回 List<User> 对象
        rpcServerProxyReturnUserList(userService);

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
}
```

## RPC 路径式调用

### 创建服务端接口
```java
@RpcService("department")
public class DepartmentService {

    @RpcMethod("get")
    public Department get(String name) {
        return new Department(1, name, new Date(), new Date());
    }

    @RpcMethod("list")
    public List<Department> list() {
        List<Department> list = new ArrayList<>();
        list.add(new Department(1, "行政部", new Date(), new Date()));
        list.add(new Department(2, "财务部", new Date(), new Date()));
        list.add(new Department(3, "技术部", new Date(), new Date()));
        list.add(new Department(4, "人事部", new Date(), new Date()));
        list.add(new Department(5, "运营部", new Date(), new Date()));
        list.add(new Department(6, "公关部", new Date(), new Date()));
        return list;
    }
}
```

### RPC客户端调用
```java
@RpcApplication
public class MockRpc {
    private static final String MockRpcClient = "MockRpcClient";

    public static void main(String[] args) throws Exception {
        RpcRunner.run(MockRpc.class, args);

        Thread.sleep(1500);

        // 路径调用，返回 Department 对象
        rpcPathReturnDepartment();

        // 路径调用，返回 Department[] 数组对象
        // rpcPathReturnDepartmentArray();

        // 路径调用，返回 List<Department> 集合对象
        rpcPathReturnDepartmentList();
    }

    private static void rpcPathReturnDepartment() {
        Department department = RpcClient.get(MockRpcClient, "/department/get", Department.class, "技术部");
        System.err.println("路径式同步调用5 =====> " + department);

        RpcClient.get(MockRpcClient, "/department/get", Department.class, (data) -> {
            System.err.println("路径式异步调用6 =====> " + data);
        }, "技术部");
    }


    private static void rpcPathReturnDepartmentArray() {
        Department[] departmentList = RpcClient.get(MockRpcClient, "/department/list", Department[].class);
        for (Department dept : departmentList) {
            System.err.println("路径式同步调用7 =====> " + dept.getId() + "---" + dept.getName());
        }

        RpcClient.get(MockRpcClient, "/department/list", Department[].class, (list) -> {
            for (Department department : list) {
                System.err.println("路径式异步调用8 =====> " + department.getId() + "---" + department.getName());
            }
        });
    }


    private static void rpcPathReturnDepartmentList() {
        RpcClient.get(MockRpcClient, "/department/list", new ValueType<List<Department>>() {
        }, (list) -> {
            for (Department department : list) {
                System.err.println("路径式异步调用9 =====> " + department.getId() + "---" + department.getName());
            }
        });
    }

}
```

## 切换协议序列化方式

RPC 框架支持 `Json` 和 `Hessian` 两种序列化方式，开发者可以通过修改配置的方式，自由切换序列化方式，无需改动代码文件。

```yaml
rpc:
  client:
    serialize: 1
```

配置文件中，`rpc.client.serialize` 属性为 RPC 实例支持的序列化方式，配置值和序列化对应方式如下：

| 配置值 | 序列化方式 |
| --- | --- |
| 1 | Json |
| 2 | Hessian |

## 注意

1、rpc的调用规则为相同的包名调用，服务端和客户端的方法接口必须要在相同的包名下。

2、rpc的服务端接口必须要注解`@RpcService`，客户端接口必须要注解`@RpcApi`。

3、rpc的异步调用，接口方法参数中多一个`RpcCallback`的函数式接口参数，用于处理rpc的异步回调事件，服务端该参数为`null`。

4、如果使用 Hessian 作为序列化工具，所有需要被序列化的实体类，都要实现 `java.io.Serializable` 接口，否则无法序列化。

## 未来预期功能

- 完整的 RPC 性能监控功能
- 运行时 RPC 操作
    - 配置查询及更新
    - 实时性能监控