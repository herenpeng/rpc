# RPC 框架

> 基于 Netty4 的一个简易的 RPC 框架
>
> 项目地址：https://github.com/herenpeng/rpc.git

## RPC 入门

RPC 框架的入门，首先要使用这个 RPC 框架，搭建一个简单的 RPC 环境，使得 RPC 客户端可以通过 RPC 协议远程调用 RPC 服务端。

1、从 GitHub 上下载项目，项目地址：`https://github.com/herenpeng/rpc.git`

2、使用 Maven 将项目安装到本地仓库中。

3、搭建两个简单的 Maven 工程，项目名称分别为：

- rpc-server-demo
- rpc-client-demo

在两个项目中，分别引入 rpc 框架的依赖：

```xml
<dependencies>
    <!--rpc框架依赖-->
    <dependency>
        <groupId>org.herr</groupId>
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

> 在 `rpc-server-demo` 项目下创建 `com.herenpeng.rpc` 文件包，并在该包下创建 `MockRpcServer` 和 `UserService` 类。

### 初始化服务端

```java
package com.herenpeng.rpc;

import com.herenpeng.rpc.server.RpcServer;

public class MockRpcServer {

    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer();
        rpcServer.start(12345);
    }

}
```

### 创建服务端接口

```java
package com.herenpeng.rpc;

import com.herenpeng.rpc.annotation.RpcServerApi;

@RpcServerApi
public class UserService {

    public String getData() {
        return "RPC模拟服务端响应数据，响应时间：" + System.currentTimeMillis();
    }

}
```

## 创建RPC客户端

> 在 `rpc-client-demo` 项目下创建 `com.herenpeng.rpc` 文件包，并在该包下创建 `MockRpcClient` 类和 `UserService` 接口。

### 初始化客户端

```java
package com.herenpeng.rpc;

import com.herenpeng.rpc.client.RpcClient;

import static com.herenpeng.rpc.util.RpcUtils.panic;

public class MockRpcClient {

    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient();
        rpcClient.register("MockServer", "127.0.0.1", 12345);
    }
}

```

### 创建客户端接口

```java
package com.herenpeng.rpc;

import com.herenpeng.rpc.annotation.RpcClientApi;
import com.herenpeng.rpc.client.RpcCallback;

@RpcClientApi
public interface UserService {

    // 同步接口
    String getData();

    // 异步接口
    String getData(RpcCallback callback);

}
```

## RPC调用

> 先启动 `rpc-server-demo` 项目下的 `MockRpcServer` 类，然后启动 `rpc-client-demo` 项目下的 `MockRpcClient` 类。

```java
package com.herenpeng.rpc;

import com.herenpeng.rpc.client.RpcClient;

import static com.herenpeng.rpc.util.RpcUtils.panic;

public class MockRpcClient {

    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient();
        rpcClient.register("MockServer", "127.0.0.1", 12345);

        UserService userService = rpcClient.createSyncRpc("MockServer", UserService.class);
        String syncData = userService.getData();
        System.out.println("同步回调");
        System.out.println(syncData);

        userService = rpcClient.createAsyncRpc("MockServer", UserService.class);
        userService.getData((data, e) -> {
            panic(e);
            System.out.println("异步回调");
            System.out.println(data);
        });
    }
}
```

## 注意

1、rpc的调用规则为相同的包名调用，服务端和客户端的方法接口必须要在相同的包名下。

2、rpc的服务端接口必须要注解 `@RpcServerApi`，客户端接口必须要注解 `@RpcClientApi`。

3、rpc的异步调用，客户端必须要比服务端的方法多一个 `RpcCallback` 的函数式接口参数，用于处理rpc的异步回调事件。
