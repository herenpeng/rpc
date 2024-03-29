package com.herenpeng.rpc.server;

import com.herenpeng.rpc.common.RpcMethodInvoke;
import com.herenpeng.rpc.common.RpcServerMonitor;
import com.herenpeng.rpc.config.RpcConfig;
import com.herenpeng.rpc.config.RpcServerConfig;
import com.herenpeng.rpc.internal.InternalCmdHandler;
import com.herenpeng.rpc.kit.DateKit;
import com.herenpeng.rpc.kit.RpcKit;
import com.herenpeng.rpc.kit.thread.RpcThreadFactory;
import com.herenpeng.rpc.protocol.ProtocolDecoder;
import com.herenpeng.rpc.protocol.ProtocolEncoder;
import com.herenpeng.rpc.protocol.content.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author herenpeng
 */
@Getter
@Slf4j
public class RpcServer {

    /**
     * 本身服务实例
     */
    private final RpcServer instance;
    private RpcConfig config;
    private RpcServerConfig serverConfig;
    private RpcServerCache cache;
    private String name;
    /**
     * rpc性能数据监控对象
     */
    private final RpcServerMonitor serverMonitor;
    private ExecutorService service;


    public RpcServer() {
        this.instance = this;
        serverMonitor = new RpcServerMonitor();
    }

    /**
     * 使用指定的端口启动rpc服务器（优先级大于配置端口）
     *
     * @param rpcApplicationClass 需要扫描的Root类
     */
    public void start(Class<?> rpcApplicationClass, RpcServerConfig serverConfig, List<Class<?>> classList) {
        long start = DateKit.now();
        this.serverConfig = serverConfig;
        serverMonitor.setMonitorMinuteLimit(serverConfig.getMonitorMinuteLimit());
        this.name = serverConfig.getName();
        log.info("[RPC服务端]{}：正在初始化", name);
        // 初始化rpc缓存
        initRpcCache(classList);
        // 初始化rpc服务端
        initRpcServer(serverConfig.getPort());
        long end = DateKit.now();
        log.info("[RPC服务端]{}：初始化完成，端口：{}，共耗时{}毫秒", name, serverConfig.getPort(), end - start);
        serverMonitor.setStartUpTime(end);
    }

    private void initRpcCache(List<Class<?>> classList) {
        cache = new RpcServerCache();
        cache.initMethodInvoke(classList);
        log.info("[RPC服务端]{}：缓存初始化完成", name);
    }

    private void initRpcServer(int port) {
        // 1.定义服务类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 2.定义执行线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(new RpcThreadFactory(RpcServer.class.getSimpleName() + "-boss"));
        EventLoopGroup workerGroup = new NioEventLoopGroup(serverConfig.getWorkerThreadNum(), new RpcThreadFactory(RpcServer.class.getSimpleName() + "-worker"));
        // 3.设置线程池
        serverBootstrap.group(bossGroup, workerGroup);
        // 4.设置通道
        serverBootstrap.channel(NioServerSocketChannel.class);
        // 5.添加Handler
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel channel) {
                ChannelPipeline pipeline = channel.pipeline();
                // 这里设置通过增加包头表示报文长度来避免粘包，最大长度为100M
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1024 * 1024 * 100, 0, 4, 0, 4));
                pipeline.addLast("decoder", new ProtocolDecoder());
                // 这里设置读取报文的包头长度来避免粘包
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                pipeline.addLast("encoder", new ProtocolEncoder());
                pipeline.addLast("handler", new RpcServerHandler(instance));
            }
        });
        try {
            // 6.绑定端口
            serverBootstrap.bind(new InetSocketAddress(port));
        } catch (Exception e) {
            e.printStackTrace();
        }
        service = new ThreadPoolExecutor(
                serverConfig.getExecutorThreadNum(),
                serverConfig.getExecutorThreadMaxNum(),
                serverConfig.getExecutorThreadKeepAliveTime(),
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(serverConfig.getExecutorThreadBlockingQueueSize()),
                new RpcThreadFactory(RpcServer.class.getSimpleName() + "-executor"));
    }


    public void handleHeartbeat(RpcRequest<?> request, ChannelHandlerContext ctx) {
        RpcResponse response = new RpcResponse(request.getSubType(), request.getSequence(), request.getSerialize(),
                serverConfig.getCompressEnableSize());
        ctx.writeAndFlush(response);
        if (this.serverConfig.isHeartbeatLogEnable()) {
            log.info("[RPC服务端]{}：接收心跳消息，消息序列号：{}", name, response.getSequence());
        }
    }


    public void invoke(RpcRequest<?> request, ChannelHandlerContext ctx) {
        if (request == null) {
            throw new IllegalArgumentException("[RPC服务端]" + name + "：request不允许为null");
        }
        int cmd = request.getCmd();
        String clientIp = RpcKit.getClientIp(ctx);
        serverMonitor.addRequest(clientIp, cmd);
        service.execute(() -> {
            long useTime = 0;
            boolean success = false;
            RpcResponse response = new RpcResponse(request.getSubType(), request.getSequence(),
                    request.getSerialize(), serverConfig.getCompressEnableSize());
            try {
                RpcMethodInvoke methodInvoke = cache.getMethodInvoke(cmd);
                Object rpcServer = methodInvoke.getRpcServer();
                Method method = methodInvoke.getMethod();
                Object[] params = request.getParams(method.getGenericParameterTypes());
                // 执行方法
                long startTime = System.currentTimeMillis();
                Object returnData = method.invoke(rpcServer, params);
                useTime = System.currentTimeMillis() - startTime;
                success = true;
                response.setReturnData(returnData);
            } catch (Exception e) {
                log.error("[RPC服务端]{}：服务端执行方法发生异常：{}", name, request);
                Throwable exception = e;
                if (e instanceof InvocationTargetException) {
                    exception = ((InvocationTargetException) e).getTargetException();
                }
                if (exception != null) {
                    response.setException(exception.getMessage());
                    exception.printStackTrace();
                }
            }
            ctx.writeAndFlush(response);
            log.info("[RPC服务端]{}：响应RPC请求消息，cmd：{}，消息序列号：{}", name, cmd, request.getSequence());
            if (success) {
                serverMonitor.addSuccess(clientIp, cmd, useTime);
            } else {
                serverMonitor.addFail(clientIp, cmd);
            }
        });
    }


    public void handleInternal(RpcRequest<?> request, ChannelHandlerContext ctx) {
        RpcResponse response = InternalCmdHandler.invoke(this, request);
        ctx.writeAndFlush(response);
        if (this.serverConfig.isHeartbeatLogEnable()) {
            log.info("[RPC服务端]{}：接收内部消息，cmd:{}，消息序列号：{}", name, request.getCmd(), response.getSequence());
        }
    }


}
