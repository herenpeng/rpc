package com.herenpeng.rpc.client;

import com.herenpeng.rpc.annotation.RpcApi;
import com.herenpeng.rpc.common.RpcInfo;
import com.herenpeng.rpc.common.RpcMethodLocator;
import com.herenpeng.rpc.config.RpcClientConfig;
import com.herenpeng.rpc.config.RpcConfig;
import com.herenpeng.rpc.config.RpcConfigProcessor;
import com.herenpeng.rpc.exception.RpcException;
import com.herenpeng.rpc.kit.*;
import com.herenpeng.rpc.kit.thread.RpcScheduler;
import com.herenpeng.rpc.kit.thread.RpcThreadFactory;
import com.herenpeng.rpc.proto.ProtocolDecoder;
import com.herenpeng.rpc.proto.ProtocolEncoder;
import com.herenpeng.rpc.proto.content.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author herenpeng
 */
@Slf4j
public class RpcServerProxy implements InvocationHandler {

    private Channel session;
    private final RpcServerProxy instance;
    private final String name;
    private final String host;
    private final int port;

    private RpcConfig config;
    private RpcClientConfig clientConfig;
    private RpcClientCache cache;

    private final Map<Integer, RpcRsp> rspEvents = new ConcurrentHashMap<>();
    /**
     * 异步请求回调事件
     */
    private final Map<Integer, RpcInfo> callbackEvents = new ConcurrentHashMap<>();

    public void setRpcRsp(int sequence, RpcRsp rpcRsp) {
        if (callbackEvents.containsKey(sequence)) {
            // 异步事件，检查回调函数
            checkCallback(sequence, rpcRsp);
        } else {
            rspEvents.put(sequence, rpcRsp);
        }
    }

    public RpcServerProxy(String name, String host, int port, Class<?> rpcScannerClass) {
        log.info("[RPC客户端]{}服务代理正在初始化", name);
        this.instance = this;
        this.name = name;
        this.host = host;
        this.port = port;
        init(rpcScannerClass);
    }

    public void init(Class<?> rpcScannerClass) {
        long start = System.currentTimeMillis();
        // 初始化客户端配置
        initRpcConfig();
        // 初始化客户端缓存
        initRpcClientCache(rpcScannerClass);
        // 初始化服务端代理
        initRpcServerProxy();
        // 初始化定时任务
        initRpcSchedule();
        long end = System.currentTimeMillis();
        log.info("[RPC客户端]{}服务代理初始化完成，已创建{}服务代理，主机：{}，端口：{}，共耗时{}毫秒", name, name, host, port, end - start);
    }

    private void initRpcConfig() {
        RpcConfigProcessor processor = new RpcConfigProcessor();
        this.config = processor.getRpc();
        this.clientConfig = this.config == null ? new RpcClientConfig() : this.config.getClient();
        log.info("[RPC客户端]配置初始化完成，配置信息：{}", config);
    }

    private void initRpcClientCache(Class<?> rpcScannerClass) {
        this.cache = new RpcClientCache();
        String packageName = rpcScannerClass.getPackageName();
        ClassScanner scanner = new ClassScanner(packageName, (clazz) -> clazz.getAnnotation(RpcApi.class) != null);
        List<Class<?>> classList = scanner.listClass();
        this.cache.initMethodLocator(classList);
        log.info("[RPC客户端]缓存初始化完成");
    }


    private Bootstrap bootstrap;

    private void initRpcServerProxy() {
        // 初始化RPC客户端
        // 1.定义客户端类
        bootstrap = new Bootstrap();
        // 2.定义执行线程组
        EventLoopGroup group = new NioEventLoopGroup(new RpcThreadFactory(RpcServerProxy.class.getSimpleName()));
        // 3.设置线程池
        bootstrap.group(group);
        // 4.设置通道
        bootstrap.channel(NioSocketChannel.class);
        // 5.添加Handler
        bootstrap.handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) {
                ChannelPipeline pipeline = channel.pipeline();
                // 这里设置通过增加包头表示报文长度来避免粘包
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1024, 0, 2, 0, 2));
                pipeline.addLast("decoder", new ProtocolDecoder());
                // 这里设置读取报文的包头长度来避免粘包
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(2));
                pipeline.addLast("encoder", new ProtocolEncoder());
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
        long startTime = System.currentTimeMillis();
        RpcMethodLocator methodLocator = cache.getMethodLocator(method);
        RpcInfo rpcInfo = invokeStart(startTime, methodLocator);
        if (!session.isActive()) {
            invokeEnd(rpcInfo, false);
            throw new RpcException("[RPC客户端]初始化失败，Socket Channel未激活，请重新初始化客户端");
        }

        RpcReq rpcReq = new RpcReq();
        rpcReq.setMethodLocator(methodLocator);
        // 设置参数
        RpcCallback<?> callback = RpcKit.getRpcCallback(args, methodLocator.isAsync());
        rpcReq.setParams(args);
        RpcProto msg = new RpcProto(RpcProto.TYPE_REQ, rpcReq);
        this.session.writeAndFlush(msg);

        // 记录信息
        rpcInfo.setParams(args);
        // 异步调用
        if (methodLocator.isAsync()) {
            rpcInfo.setCallable(callback);
            callbackEvents.put(msg.getSequence(), rpcInfo);
            return null;
        }
        // 同步调用
        RpcRsp rpcRsp;
        while (true) {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - startTime) > clientConfig.getSyncTimeout()) {
                invokeEnd(rpcInfo, false);
                return null;
            }
            if ((rpcRsp = rspEvents.remove(msg.getSequence())) != null) {
                if (StringUtils.isNotEmpty(rpcRsp.getException())) {
                    invokeEnd(rpcInfo, false);
                    throw new RpcException("[RPC客户端]RPC服务端响应异常信息：" + rpcRsp.getException());
                }
                rpcInfo.setReturnData(rpcRsp.getReturnData());
                invokeEnd(rpcInfo, true);
                return rpcRsp.getReturnData();
            }
        }
    }

    /**
     * 检查RPC异步回调信息
     *
     * @param sequence
     * @param rpcRsp
     */
    private void checkCallback(int sequence, RpcRsp rpcRsp) {
        RpcInfo rpcInfo = callbackEvents.get(sequence);
        if (rpcInfo == null) {
            return;
        }
        if (StringUtils.isNotEmpty(rpcRsp.getException())) {
            invokeEnd(rpcInfo, false);
            throw new RpcException("[RPC客户端]RPC服务端响应异常信息：" + rpcRsp.getException());
        }
        RpcCallback callback = rpcInfo.getCallable();
        if (callback == null) {
            return;
        }
        rpcInfo.setReturnData(rpcRsp.getReturnData());
        invokeEnd(rpcInfo, true);
        // 异步回调，使用线程池执行 runnable 接口
        callback.execute(rpcRsp.getReturnData());
    }


    private void initRpcSchedule() {
        // 初始化心跳
        initHeartbeat();
        log.info("[RPC客户端]端{}服务代理初始化定时任务成功", name);
    }

    private final Queue<Integer> clientHeartbeatQueue = new ConcurrentLinkedQueue<>();

    private void initHeartbeat() {
        RpcScheduler.doLoopTask(() -> {
            checkHeartbeat();
            RpcProto rpcProto = new RpcProto(RpcProto.TYPE_EMPTY);
            clientHeartbeatQueue.offer(rpcProto.getSequence());
            // 构造一个消息
            this.session.writeAndFlush(rpcProto);
            if (this.clientConfig.isHeartbeatLogEnable()) {
                log.info("[RPC客户端]发送心跳消息：消息序列号：{}", rpcProto.getSequence());
            }
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
                if (this.clientConfig.isHeartbeatLogEnable()) {
                    log.info("[RPC客户端]确认心跳消息，消息序列号：{}", clientHeartbeat);

                }
                return;
            }
        }
    }


    private RpcInfo invokeStart(long startTime, RpcMethodLocator locator) {
        RpcInfo rpcInfo = new RpcInfo();
        rpcInfo.setStartTime(startTime);
        rpcInfo.setMethodLocator(locator);
        return rpcInfo;
    }

    private void invokeEnd(RpcInfo rpcInfo, boolean success) {
        rpcInfo.setEndTime(System.currentTimeMillis());
        rpcInfo.setSuccess(success);
        // 记录，打印日志
        RpcMethodLocator locator = rpcInfo.getMethodLocator();
        if (clientConfig.isMonitorLogEnable()) {
            log.info("[RPC客户端]执行结果：目标：{}#{}{}，是否异步：{}，是否成功，{}，入参：{}，出参：{}，消耗时间：{}ms",
                    locator.getClassName(), locator.getMethodName(), locator.getParamTypeNames(), locator.isAsync(),
                    rpcInfo.isSuccess(), rpcInfo.getParams(), rpcInfo.getReturnData(), rpcInfo.getEndTime() - rpcInfo.getStartTime());
        }
    }


}
