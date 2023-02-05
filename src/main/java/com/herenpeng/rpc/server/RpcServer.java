package com.herenpeng.rpc.server;

import com.herenpeng.rpc.RpcDecoder;
import com.herenpeng.rpc.RpcEncoder;
import com.herenpeng.rpc.annotation.RpcApi;
import com.herenpeng.rpc.annotation.RpcService;
import com.herenpeng.rpc.kit.ClassScanner;
import com.herenpeng.rpc.proto.RpcReq;
import com.herenpeng.rpc.proto.RpcRsp;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author herenpeng
 */
public class RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private RpcServerCache cache = new RpcServerCache();

    /**
     * 本身服务实例
     */
    private final RpcServer instance;

    public RpcServer() {
        this.instance = this;
    }

    public void start(int port, Class<?> rpcScanClass) {
        logger.info("[RPC服务端]正在初始化");
        long start = System.currentTimeMillis();
        // 初始化rpc接口
        initRpcApi(rpcScanClass);
        // 初始化rpc服务端
        initRpcServer(port);
        long end = System.currentTimeMillis();
        logger.info("[RPC服务端]初始化完成，端口：{}，共耗时{}毫秒", port, end - start);
    }

    private void initRpcApi(Class<?> rpcScanClass) {
        if (rpcScanClass == null) {
            logger.warn("[RPC服务端]rpc接口包扫描类对象为空");
            return;
        }
        String packageName = rpcScanClass.getPackageName();
        ClassScanner scanner = new ClassScanner(packageName);
        List<Class<?>> classList = scanner.listClass();
        filterRpcApi(classList);
    }

    private void filterRpcApi(List<Class<?>> classList) {
        List<Class<?>> apiClassList = new ArrayList<>();
        List<Class<?>> serviceClassList = new ArrayList<>();
        classList.forEach((clazz) -> {
            if (clazz.getAnnotation(RpcApi.class) != null) {
                apiClassList.add(clazz);
            }
            if (clazz.getAnnotation(RpcService.class) != null) {
                serviceClassList.add(clazz);
            }
        });
        // 接口和实现的映射
        for (Class<?> apiClass : apiClassList) {
            for (Class<?> serviceClass : serviceClassList) {
                if (apiClass.isAssignableFrom(serviceClass)) {
                    // apiClass是serviceClass的接口
                    cache.setApiService(apiClass, serviceClass);
                }
            }
        }
    }

    private void initRpcServer(int port) {
        // 1.定义服务类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 2.定义执行线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        // 3.设置线程池
        serverBootstrap.group(bossGroup, workerGroup);
        // 4.设置通道
        serverBootstrap.channel(NioServerSocketChannel.class);
        // 5.添加Handler
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel channel) {
                ChannelPipeline pipeline = channel.pipeline();
                // 这里设置通过增加包头表示报文长度来避免粘包
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1024, 0, 2, 0, 2));
                pipeline.addLast("decoder", new RpcDecoder());
                // 这里设置读取报文的包头长度来避免粘包
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(2));
                pipeline.addLast("encoder", new RpcEncoder());
                pipeline.addLast("handler", new RpcServerHandler(instance));
            }
        });
        try {
            // 6.绑定端口
            serverBootstrap.bind(new InetSocketAddress(port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public RpcRsp invoke(RpcReq rpcReq) {
        if (rpcReq == null) {
            throw new IllegalArgumentException("[RPC服务端]rpcReq不允许为null");
        }
        RpcRsp rpcRsp = new RpcRsp();
        try {
            Object rpcServer = cache.getRpcServer(rpcReq.getClassName());
            Class<?> rpcServerClass = rpcServer.getClass();
            String[] paramTypeNames = rpcReq.getParamTypeNames();
            Method method = rpcServerClass.getMethod(rpcReq.getMethodName(), cache.getParamTypes(paramTypeNames));
            // 执行方法
            Object returnData = method.invoke(rpcServer, rpcReq.getParams());
            rpcRsp.setReturnData(returnData);
        } catch (Exception e) {
            logger.error("[RPC服务端]服务端执行方法发生异常");
            Throwable exception = e;
            if (e instanceof InvocationTargetException) {
                exception = ((InvocationTargetException) e).getTargetException();
            }
            if (exception != null) {
                rpcRsp.setException(exception.getMessage());
                exception.printStackTrace();
            }
        }
        return rpcRsp;
    }


}
