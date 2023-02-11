package com.herenpeng.rpc.client;

import com.herenpeng.rpc.annotation.RpcApi;
import com.herenpeng.rpc.common.RpcMethodLocator;
import com.herenpeng.rpc.config.RpcClientConfig;
import com.herenpeng.rpc.exception.RpcException;
import com.herenpeng.rpc.kit.*;
import com.herenpeng.rpc.proto.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author herenpeng
 */
@Slf4j
public class RpcServerProxy implements InvocationHandler {

    // private static final Logger log = LoggerFactory.getLogger(RpcServerProxy.class);

    /**
     * RPC 请求序列号
     */
    private final AtomicInteger rpcReqSequence = new AtomicInteger();

    private Channel session;
    private final RpcServerProxy instance;
    private final String name;
    private final String host;
    private final int port;
    private final RpcClientConfig clientConfig;

    private final RpcClientCache clientCache;


    // private final InvocationHandler asyncRpcServer;
    // private final InvocationHandler syncRpcServer;

    // public InvocationHandler get(boolean async) {
    //     return async ? asyncRpcServer : syncRpcServer;
    // }

    private static final Map<Integer, RpcRsp> rspEvents = new ConcurrentHashMap<>();
    /**
     * 异步请求回调事件
     */
    private static final Map<Integer, RpcCallback> callbackEvents = new ConcurrentHashMap<>();

    public void setRpcRsp(int sequence, RpcRsp rpcRsp) {
        if (callbackEvents.containsKey(sequence)) {
            // 异步事件，检查回调函数
            checkCallback(sequence, rpcRsp);
        } else {
            rspEvents.put(sequence, rpcRsp);
        }
    }

    public RpcServerProxy(String name, String host, int port, RpcClientConfig clientConfig) {
        log.info("[RPC客户端]{}服务代理正在初始化", name);
        this.instance = this;
        this.name = name;
        this.host = host;
        this.port = port;
        // if (rpcConfig == null) {
        //     // 使用默认配置
        //     rpcConfig = ;
        // }
        this.clientConfig = clientConfig == null ? new RpcClientConfig() : clientConfig;
        this.clientCache = new RpcClientCache();
        log.info("[RPC客户端]配置初始化完成，配置信息：{}", clientConfig);
        // 初始化异步代理类和同步代理类
        // this.asyncRpcServer = new RpcServerAsyncProxy(this);
        // this.syncRpcServer = new RpcServerSyncProxy(this);
        init();
    }

    public void init() {
        long start = System.currentTimeMillis();
        initRpcServerProxy();
        initRpcSchedule();
        long end = System.currentTimeMillis();
        log.info("[RPC客户端]{}服务代理初始化完成，已创建{}服务代理，主机：{}，端口：{}，共耗时{}豪秒", name, name, host, port, end - start);
    }

    private static Bootstrap bootstrap;

    private void initRpcServerProxy() {
        // 初始化RPC客户端
        // 1.定义客户端类
        bootstrap = new Bootstrap();
        // 2.定义执行线程组
        EventLoopGroup group = new NioEventLoopGroup();
        // 3.设置线程池
        bootstrap.group(group);
        // 4.设置通道
        bootstrap.channel(NioSocketChannel.class);
        // 5.添加Handler
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) {
                ChannelPipeline pipeline = channel.pipeline();
                // 这里设置通过增加包头表示报文长度来避免粘包
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1024, 0, 2, 0, 2));
                pipeline.addLast("decoder", new RpcDecoder());
                // 这里设置读取报文的包头长度来避免粘包
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(2));
                pipeline.addLast("encoder", new RpcEncoder());
                pipeline.addLast("handler", new RpcClientHandler(instance));
            }
        });
        connection();
    }

    private void connection() {
        try {
            if (this.session == null || !this.session.isActive()) {
                // 6.建立连接
                log.info("[RPC客户端]正在链接，主机：{}，端口：{}", host, port);
                ChannelFuture channelFuture = bootstrap.connect(host, port);
                this.session = channelFuture.channel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 3秒后检查一下通道是否链接，未链接则重新链接
        RpcScheduler.doTask(() -> {
            if (this.session.isActive()) {
                log.info("[RPC客户端]链接成功，主机：{}，端口：{}", host, port);
            } else {
                log.error("[RPC客户端]链接失败，准备重连，主机：{}，端口：{}", host, port);
                // 如果 Socket Channel 未激活，1秒后自动重连
                connection();
            }
        }, clientConfig.getReconnectionTime());
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (!session.isActive()) {
            throw new RpcException("[RPC客户端]初始化失败，Socket Channel未激活，请重新初始化客户端");
        }
        long startTime = System.currentTimeMillis();
        RpcMethodLocator methodLocator = clientCache.getMethodLocator(method);
        // RpcReq rpcReq = generateRpcReq(method, args, async);
        RpcReq rpcReq = new RpcReq();
        rpcReq.setClassName(methodLocator.getClassName());
        rpcReq.setMethodName(methodLocator.getMethodName());
        rpcReq.setParamTypeNames(methodLocator.getParamTypeNames());
        // 设置参数
        rpcReq.setParams(RpcKit.getMethodParams(args, methodLocator.isAsync()));
        byte[] data = JsonUtils.toBytes(rpcReq);
        RpcProto msg = new RpcProto(RpcProto.TYPE_REQ, rpcReqSequence.incrementAndGet(), data);
        this.session.writeAndFlush(msg);

        // 异步调用
        if (methodLocator.isAsync()) {
            callbackEvents.put(msg.getSequence(), (RpcCallback<?>) args[args.length - 1]);
            return null;
        }
        // 同步调用
        RpcRsp rpcRsp;
        while (true) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - startTime) > clientConfig.getSyncTimeout()) {
                return null;
            }
            if ((rpcRsp = rspEvents.remove(msg.getSequence())) != null) {
                if (StringUtils.isNotEmpty(rpcRsp.getException())) {
                    throw new RpcException("[RPC客户端]RPC服务端响应异常信息：" + rpcRsp.getException());
                }
                return rpcRsp.getReturnData();
            }
        }
    }


    // private RpcReq generateRpcReq(Method method, Object[] args, boolean async) {
    //     Class<?> rpcClientClass = method.getDeclaringClass();
    //     String rpcClientClassName = rpcClientClass.getName();
    //     if (!rpcClientClass.isInterface()) {
    //         throw new RpcException("[RPC客户端]服务代理" + rpcClientClassName + "必须是接口类型");
    //     }
    //     if (!rpcClientClass.isAnnotationPresent(RpcApi.class)) {
    //         throw new RpcException("[RPC客户端]服务代理" + rpcClientClassName + "不存在，请检查@RpcApi注解");
    //     }
    //     RpcReq rpcReq = new RpcReq(rpcClientClassName, method.getName());
    //     // 异步调用的最后一个参数必须是 Runnable 接口
    //     if (async) {
    //         if (args == null || args.length < 1) {
    //             throw new RpcException("[RPC客户端]RPC异步调用至少需要一个参数，且最后一个参数必须是RpcCallback函数式接口");
    //         }
    //         Class<?> returnType = method.getReturnType();
    //         if (void.class == returnType) {
    //             throw new RpcException("[RPC客户端]RPC异步调用必须拥有函数返回值，否则回调无效");
    //         }
    //         Object callback = args[args.length - 1];
    //         if (RpcCallback.class.isAssignableFrom(callback.getClass())) {
    //             rpcReq.setCallback((RpcCallback) args[args.length - 1]);
    //         } else {
    //             throw new RpcException("[RPC客户端]RPC异步调用的最后一个参数必须是RpcCallback函数式接口");
    //         }
    //     }
    //
    //     if (args != null && args.length != 0) {
    //         // 如果是异步调用，最后一个参数不入参
    //         int length = async ? args.length - 1 : args.length;
    //         String[] paramTypeNames = new String[length];
    //         Object[] params = new Object[length];
    //         for (int i = 0; i < length; i++) {
    //             Object arg = args[i];
    //             String paramTypeName = arg.getClass().getName();
    //             paramTypeNames[i] = paramTypeName;
    //             params[i] = args[i];
    //         }
    //         rpcReq.setParamTypeNames(paramTypeNames);
    //         rpcReq.setParams(params);
    //     }
    //     return rpcReq;
    // }

    // 检查RPC异步回调信息
    private <T> void checkCallback(int sequence, RpcRsp<T> rpcRsp) {
        RpcCallback<T> callback = callbackEvents.get(sequence);
        if (callback == null) {
            return;
        }
        // 异步回调，使用线程池执行 runnable 接口
        String exception = rpcRsp.getException();
        callback.execute(rpcRsp.getReturnData(), StringUtils.isNotEmpty(exception) ?
                new RpcException(exception) : null);
    }


    private void initRpcSchedule() {
        // 初始化心跳
        initHeartbeat();
        log.info("[RPC客户端]端{}服务代理初始化定时任务成功", name);
    }

    private static final AtomicInteger heartbeatSequence = new AtomicInteger();
    private static final Queue<Integer> clientHeartbeatQueue = new ConcurrentLinkedQueue<>();

    private void initHeartbeat() {
        RpcScheduler.doLoopTask(() -> {
            checkHeartbeat();
            clientHeartbeatQueue.offer(heartbeatSequence.incrementAndGet());
            // 构造一个消息
            RpcProto rpcMsg = new RpcProto(RpcProto.TYPE_EMPTY, heartbeatSequence.get());
            this.session.writeAndFlush(rpcMsg);
            log.info("[RPC客户端]发送心跳消息：消息序列号：{}", rpcMsg.getSequence());
        }, clientConfig.getHeartbeatTime(), clientConfig.getHeartbeatTime());
    }

    private void checkHeartbeat() {
        if (clientHeartbeatQueue.size() > clientConfig.getHeartbeatInvalidTimes() && !this.session.isActive()) {
            // 服务端超过3次没有响应心跳数据，尝试重新链接服务端
            connection();
        }
    }

    // 确认心跳信息
    public void confirmHeartbeat(int sequence) {
        while (true) {
            Integer clientHeartbeat = clientHeartbeatQueue.peek();
            if (clientHeartbeat == null) {
                // 可能是客户端重启导致心跳丢失，不重要
                continue;
            }
            if (clientHeartbeat > sequence) {
                return;
            }
            // 如果客户端取出来的心跳消息序列号小于等于服务端响应的心跳消息序列号，直接移除客户端的心跳数据
            clientHeartbeatQueue.poll();
            if (clientHeartbeat == sequence) {
                log.info("[RPC客户端]确认心跳消息，消息序列号：{}", clientHeartbeat);
                return;
            }
        }
    }


    // private static class RpcServerAsyncProxy implements InvocationHandler {
    //
    //     private final RpcServerProxy rpcServerProxy;
    //
    //     public RpcServerAsyncProxy(RpcServerProxy rpcServerProxy) {
    //         this.rpcServerProxy = rpcServerProxy;
    //     }
    //
    //     @Override
    //     public Object invoke(Object proxy, Method method, Object[] args) {
    //         // 异步执行
    //         return rpcServerProxy.invoke(method, args, true);
    //     }
    //
    // }
    //
    // private static class RpcServerSyncProxy implements InvocationHandler {
    //
    //     private final RpcServerProxy rpcServerProxy;
    //
    //     public RpcServerSyncProxy(RpcServerProxy rpcServerProxy) {
    //         this.rpcServerProxy = rpcServerProxy;
    //     }
    //
    //     @Override
    //     public Object invoke(Object proxy, Method method, Object[] args) {
    //         // 同步执行
    //         return rpcServerProxy.invoke(method, args, false);
    //     }
    //
    // }


}
