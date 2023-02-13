# RPC 框架

> 基于 Netty4 的一个简易的 RPC 框架
>
> 项目地址：https://github.com/herenpeng/rpc.git

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

## 创建RPC服务端

> 在项目下创建`com.herenpeng.rpc`文件包，并在该包下创建`MockRpcServer`、、`UserService`接口和`UserServiceImpl`实现类。

### 初始化服务端

```java
package com.herenpeng.rpc;

import com.herenpeng.rpc.server.RpcServer;

public class MockRpcServer {

    public static void main(String[] args) {

        RpcServer rpcServer = new RpcServer();
        rpcServer.start(10000, MockRpcServer.class);
    }

}
```

### 创建服务端接口及其实现类

```java
package com.herenpeng.rpc;

import com.herenpeng.rpc.annotation.RpcApi;import com.herenpeng.rpc.kit.RpcCallback;

@RpcApi
public interface UserService {

    void setUsername(String name);

    String getUsername(String name);

    String getUsername(String name, RpcCallback callback);

}
```

```java
@RpcService
public class UserServiceImpl implements UserService {

    @Override
    public void setUsername(String name) {
        System.out.println("Hello,World:" + name);
    }

    @Override
    public String getUsername(String name) {
        return "RPC调用，调用参数：" + name + "，响应回调时间：" + System.currentTimeMillis();
    }

    @Override
    public String getUsername(String name, RpcCallback callback) {
        return getUsername(name);
    }

}
```

## 创建RPC客户端

> 在项目下创建`com.herenpeng.rpc`文件包，并在该包下创建`MockRpcClient`类和 `UserService` 接口，其中`UserService`可以和服务端使用同一个接口文件。

### 初始化客户端

```java
package com.herenpeng.rpc;

import com.herenpeng.rpc.client.RpcClient;

public class MockRpcClient {

    private static final String MockRpcServer = "MockRpcServer";

    public static void main(String[] args) throws InterruptedException {
        // 创建客户端并调用方法
        RpcClient rpcClient = new RpcClient();
        rpcClient.register(MockRpcServer, "127.0.0.1", 10000, MockRpcClient.class);
    }
}
```

## RPC调用

> 先启动`MockRpcServer`类，然后启动`MockRpcClient`类。

```java
package com.herenpeng.rpc;

import com.herenpeng.rpc.client.RpcClient;

import static com.herenpeng.rpc.kit.RpcKit.panic;

public class MockRpcClient {

    private static final String MockRpcServer = "MockRpcServer";

    public static void main(String[] args) throws InterruptedException {
        // 创建客户端并调用方法
        RpcClient rpcClient = new RpcClient();
        rpcClient.register(MockRpcServer, "127.0.0.1", 10000, MockRpcClient.class);

        Thread.sleep(3000);

        UserService userService = rpcClient.createRpc(MockRpcServer, UserService.class);
        String username = userService.getUsername("肖总");
        System.out.println("同步调用：" + username);
        
        userService.getUsername("肖总", (data, e) -> {
            panic(e);
            System.out.println("异步调用：" + data);
        });
    }

}
```

## 注意

1、rpc的调用规则为相同的包名调用，服务端和客户端的方法接口必须要在相同的包名下。

2、rpc的服务端接口必须要注解`@RpcService`，客户端接口必须要注解`@RpcApi`。

3、rpc的异步调用，接口方法参数中多一个`RpcCallback`的函数式接口参数，用于处理rpc的异步回调事件，服务端该参数为`null`。
