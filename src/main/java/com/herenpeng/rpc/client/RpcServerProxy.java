package com.herenpeng.rpc.client;

import com.herenpeng.rpc.*;
import com.herenpeng.rpc.annotation.RpcClientApi;
import com.herenpeng.rpc.exception.RpcException;
import com.herenpeng.rpc.util.RpcScheduler;
import com.herenpeng.rpc.util.StringUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author herenpeng
 */
public class RpcServerProxy {

    private static final Logger logger = LoggerFactory.getLogger(RpcServerProxy.class);

    // RPC 请求 Id
    private final AtomicLong rpcReqId = new AtomicLong();

    protected Channel session;
    private final RpcServerProxy instance;
    private final String name;
    private final String host;
    private final int port;
    private final RpcClientConfig rpcConfig;

    private final InvocationHandler asyncRpcServer;
    private final InvocationHandler syncRpcServer;

    public InvocationHandler get(boolean async) {
        return async ? asyncRpcServer : syncRpcServer;
    }

    protected static final Map<Long, RpcRsp> rpcRspEvents = new ConcurrentHashMap<>();
    // 异步请求Id
    protected static final Map<Long, RpcCallback> rpcAsyncCallbackEvents = new ConcurrentHashMap<>();

    public void setRpcRsp(RpcRsp rpcRsp) {
        rpcRspEvents.put(rpcRsp.getId(), rpcRsp);
        // 检查回调函数
        checkCallback(rpcRsp);
    }

    public RpcServerProxy(String name, String host, int port, RpcClientConfig rpcConfig) {
        logger.info("[RPC客户端]{}服务代理正在初始化", name);
        this.instance = this;
        this.name = name;
        this.host = host;
        this.port = port;
        if (rpcConfig == null) {
            // 使用默认配置
            rpcConfig = new RpcClientConfig();
        }
        this.rpcConfig = rpcConfig;
        logger.info("[RPC客户端]配置初始化完成，配置信息：{}", rpcConfig);
        // 初始化异步代理类和同步代理类
        this.asyncRpcServer = new RpcServerAsyncProxy(this);
        this.syncRpcServer = new RpcServerSyncProxy(this);
        init();
    }

    public void init() {
        long start = System.currentTimeMillis();
        initRpcServerProxy();
        initRpcSchedule();
        long end = System.currentTimeMillis();
        logger.info("[RPC客户端]{}服务代理初始化完成，已创建{}服务代理，主机：{}，端口：{}，共耗时{}豪秒", name, name, host, port, end - start);
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
                logger.info("[RPC客户端]正在链接，主机：{}，端口：{}", host, port);
                ChannelFuture channelFuture = bootstrap.connect(host, port);
                this.session = channelFuture.channel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 3秒后检查一下通道是否链接，未链接则重新链接
        RpcScheduler.doTask(() -> {
            if (this.session.isActive()) {
                logger.info("[RPC客户端]链接成功，主机：{}，端口：{}", host, port);
            } else {
                logger.error("[RPC客户端]链接失败，准备重连，主机：{}，端口：{}", host, port);
                // 如果 Socket Channel 未激活，1秒后自动重连
                connection();
            }
        }, rpcConfig.getReconnectionTime());
    }

    // 是否异步 async
    public Object invoke(Method method, Object[] args, boolean async) {
        if (!session.isActive()) {
            throw new RpcException("[RPC客户端]初始化失败，Socket Channel未激活，请重新初始化客户端");
        }
        long startTime = System.currentTimeMillis();
        RpcReq rpcReq = generateRpcReq(method, args, async);
        this.session.writeAndFlush(rpcReq);

        // 异步调用
        if (async) {
            rpcAsyncCallbackEvents.put(rpcReq.getId(), rpcReq.getCallback());
            return null;
        }
        // 同步调用
        RpcRsp rpcRsp;
        while (true) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - startTime) > rpcConfig.getSyncTimeout()) {
                return null;
            }
            if ((rpcRsp = rpcRspEvents.get(rpcReq.getId())) != null) {
                if (StringUtils.isNotEmpty(rpcRsp.getException())) {
                    throw new RpcException("[RPC客户端]RPC服务端响应异常信息：" + rpcRsp.getException());
                }
                return rpcRsp.getReturnData();
            }
        }
    }


    private RpcReq generateRpcReq(Method method, Object[] args, boolean async) {
        Class<?> rpcClientClass = method.getDeclaringClass();
        String rpcClientClassName = rpcClientClass.getName();
        if (!rpcClientClass.isInterface()) {
            throw new RpcException("[RPC客户端]服务代理" + rpcClientClassName + "必须是接口类型");
        }
        if (!rpcClientClass.isAnnotationPresent(RpcClientApi.class)) {
            throw new RpcException("[RPC客户端]服务代理" + rpcClientClassName + "不存在，请检查@RpcClientApi注解");
        }
        RpcReq rpcReq = new RpcReq(rpcReqId, rpcClientClassName, method.getName());
        // 异步调用的最后一个参数必须是 Runnable 接口
        if (async) {
            if (args == null || args.length < 1) {
                throw new RpcException("[RPC客户端]RPC异步调用至少需要一个参数，且最后一个参数必须是RpcCallback函数式接口");
            }
            Class<?> returnType = method.getReturnType();
            if (void.class == returnType) {
                throw new RpcException("[RPC客户端]RPC异步调用必须拥有函数返回值，否则回调无效");
            }
            Object callback = args[args.length - 1];
            if (RpcCallback.class.isAssignableFrom(callback.getClass())) {
                rpcReq.setCallback((RpcCallback) args[args.length - 1]);
            } else {
                throw new RpcException("[RPC客户端]RPC异步调用的最后一个参数必须是RpcCallback函数式接口");
            }
        }

        if (args != null && args.length != 0) {
            // 如果是异步调用，最后一个参数不入参
            int length = async ? args.length - 1 : args.length;
            String[] paramTypeNames = new String[length];
            Object[] params = new Object[length];
            for (int i = 0; i < length; i++) {
                Object arg = args[i];
                String paramTypeName = arg.getClass().getName();
                paramTypeNames[i] = paramTypeName;
                params[i] = args[i];
            }
            rpcReq.setParamTypeNames(paramTypeNames);
            rpcReq.setParams(params);
        }
        return rpcReq;
    }

    // 检查RPC异步回调信息
    private void checkCallback(RpcRsp rpcRsp) {
        RpcCallback callback = rpcAsyncCallbackEvents.get(rpcRsp.getId());
        if (callback == null) {
            return;
        }
        // 异步回调，使用线程池执行 runnable 接口
        String exception = rpcRsp.getException();
        callback.run(rpcRsp.getReturnData(), StringUtils.isNotEmpty(exception) ?
                new RpcException(exception) : null);
    }


    private void initRpcSchedule() {
        // 初始化心跳
        initHeartbeat();
        logger.info("[RPC客户端]端{}服务代理初始化定时任务成功", name);
    }

    private static final AtomicLong heartbeatId = new AtomicLong();
    private static final Queue<RpcHeartbeat> clientHeartbeatQueue = new ConcurrentLinkedQueue<>();

    private void initHeartbeat() {
        RpcScheduler.doLoopTask(() -> {
            checkHeartbeat();
            RpcHeartbeat rpcHeartbeat = new RpcHeartbeat(heartbeatId);
            clientHeartbeatQueue.offer(rpcHeartbeat);
            this.session.writeAndFlush(rpcHeartbeat);
            logger.info("[RPC客户端]正在发送心跳，心跳id：{}", rpcHeartbeat.id());
        }, rpcConfig.getHeartbeatTime(), rpcConfig.getHeartbeatTime());
    }

    private void checkHeartbeat() {
        if (clientHeartbeatQueue.size() > rpcConfig.getHeartbeatInvalidTimes() && !this.session.isActive()) {
            // 服务端超过3次没有响应心跳数据，尝试重新链接服务端
            connection();
        }
    }

    // 确认心跳信息
    public void confirmHeartbeat(RpcHeartbeat rpcHeartbeat) {
        if (rpcHeartbeat == null) {
            return;
        }
        while (true) {
            RpcHeartbeat clientHeartbeat = clientHeartbeatQueue.peek();
            if (clientHeartbeat == null) {
                // 可能是客户端重启导致心跳丢失，不重要
                continue;
            }
            if (clientHeartbeat.id() > rpcHeartbeat.id()) {
                return;
            }
            // 如果客户端取出来的心跳id小于等于服务端响应的心跳id，直接移除客户端的心跳数据
            clientHeartbeatQueue.poll();
            if (clientHeartbeat.id() == rpcHeartbeat.id()) {
                logger.info("[RPC客户端]确认服务端响应的心跳数据，心跳id：{}", clientHeartbeat.id());
                return;
            }
        }
    }


}
