package com.herenpeng.rpc.server;

import com.herenpeng.rpc.common.RpcMethodInvoke;
import com.herenpeng.rpc.common.RpcMethodLocator;
import com.herenpeng.rpc.config.RpcConfig;
import com.herenpeng.rpc.config.RpcConfigProcessor;
import com.herenpeng.rpc.config.RpcServerConfig;
import com.herenpeng.rpc.proto.*;
import com.herenpeng.rpc.kit.ClassScanner;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author herenpeng
 */
@Slf4j
public class RpcServer {

    /**
     * 本身服务实例
     */
    private final RpcServer instance;
    private RpcConfig config;
    private RpcServerConfig serverConfig;
    private RpcServerCache cache;


    public RpcServer() {
        this.instance = this;
    }

    public void start(int port, Class<?> rpcScannerClass) {
        log.info("[RPC服务端]正在初始化");
        long start = System.currentTimeMillis();
        // 初始化rpc配置
        initRpcConfig();
        // 初始化rpc缓存
        initRpcCache(rpcScannerClass);
        // 初始化rpc接口
        // initRpcApi(rpcScannerClass);
        // 初始化rpc服务端
        initRpcServer(port);
        long end = System.currentTimeMillis();
        log.info("[RPC服务端]初始化完成，端口：{}，共耗时{}毫秒", port, end - start);
    }


    private void initRpcConfig() {
        RpcConfigProcessor processor = new RpcConfigProcessor();
        this.config = processor.getRpc();
        this.serverConfig = this.config == null ? new RpcServerConfig() : this.config.getServer();
        log.info("[RPC服务端]配置初始化完成，配置信息：{}", config);
    }

    private void initRpcCache(Class<?> rpcScannerClass) {
        cache = new RpcServerCache();
        if (rpcScannerClass == null) {
            log.warn("[RPC服务端]rpc接口包扫描类对象为空");
            return;
        }
        String packageName = rpcScannerClass.getPackageName();
        ClassScanner scanner = new ClassScanner(packageName);
        List<Class<?>> classList = scanner.listClass();

        cache.initMethodInvoke(classList);
        log.info("[RPC服务端]缓存初始化完成");
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


    public void handleHeartbeat(RpcProto msg, ChannelHandlerContext ctx) {
        ctx.writeAndFlush(msg);
        if (this.serverConfig.isHeartbeatLogEnable()) {
            log.info("[RPC服务端]接收心跳消息，消息序列号：{}", msg.getSequence());
        }
    }


    public RpcRsp invoke(RpcReq rpcReq) {
        if (rpcReq == null) {
            throw new IllegalArgumentException("[RPC服务端]rpcReq不允许为null");
        }
        RpcRsp rpcRsp = new RpcRsp();
        try {
            RpcMethodLocator locator = rpcReq.getMethodLocator();
            RpcMethodInvoke methodInvoke = cache.getMethodInvoke(locator);
            Object rpcServer = methodInvoke.getRpcServer();
            Method method = methodInvoke.getMethod();
            // 执行方法
            Object returnData = method.invoke(rpcServer, rpcReq.getParams());
            rpcRsp.setReturnData(returnData);
        } catch (Exception e) {
            log.error("[RPC服务端]服务端执行方法发生异常");
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
