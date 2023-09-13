package com.herenpeng.rpc.client;

import com.herenpeng.rpc.annotation.RpcApi;
import com.herenpeng.rpc.common.RpcInfo;
import com.herenpeng.rpc.common.RpcMethodLocator;
import com.herenpeng.rpc.config.RpcClientConfig;
import com.herenpeng.rpc.config.RpcConfig;
import com.herenpeng.rpc.exception.RpcException;
import com.herenpeng.rpc.internal.InternalCmdEnum;
import com.herenpeng.rpc.internal.InternalCmdHandler;
import com.herenpeng.rpc.kit.*;
import com.herenpeng.rpc.kit.thread.RpcScheduler;
import com.herenpeng.rpc.kit.thread.RpcThreadFactory;
import com.herenpeng.rpc.protocol.ProtocolDecoder;
import com.herenpeng.rpc.protocol.ProtocolEncoder;
import com.herenpeng.rpc.protocol.content.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author herenpeng
 */
@Slf4j
@Getter
public class RpcServerProxy implements InvocationHandler {

    private Channel session;
    private final RpcServerProxy instance;
    private final String name;
    private final String host;
    private final int port;

    private RpcConfig config;
    private RpcClientConfig clientConfig;
    private RpcClientCache cache;

    private final Map<Integer, CountDownLatch> syncLockMap = new ConcurrentHashMap<>();
    private final Map<Integer, RpcResponse> responseEvents = new ConcurrentHashMap<>();
    /**
     * 异步请求回调事件
     */
    private final Map<Integer, RpcInfo> callbackEvents = new ConcurrentHashMap<>();

    public void setRpcResponse(int sequence, RpcResponse response) {
        if (callbackEvents.containsKey(sequence)) {
            // 异步事件，检查回调函数
            checkCallback(sequence, response);
        } else {
            // 同步锁
            responseEvents.put(sequence, response);
            CountDownLatch latch = syncLockMap.remove(sequence);
            if (latch != null) {
                latch.countDown();
            }
        }
    }


    public RpcServerProxy(Class<?> rpcApplicationClass, RpcClientConfig clientConfig, List<Class<?>> classList) {
        // this.config = rpcConfig;
        this.clientConfig = clientConfig;
        log.info("[RPC客户端]{}：正在初始化", clientConfig.getName());
        // 获取对应的远端配置
        this.instance = this;
        this.name = clientConfig.getName();
        this.host = clientConfig.getHost();
        this.port = clientConfig.getPort();
        init(classList);
    }

    public void init(List<Class<?>> classList) {
        long start = System.currentTimeMillis();
        // 初始化客户端缓存
        initRpcClientCache(classList);
        // 初始化服务端代理
        initRpcServerProxy();
        // 初始化定时任务
        initRpcSchedule();
        long end = System.currentTimeMillis();
        log.info("[RPC客户端]{}：初始化完成，已创建{}服务代理，主机：{}，端口：{}，共耗时{}毫秒", name, name, host, port, end - start);
    }


    private void initRpcClientCache(List<Class<?>> classList) {
        this.cache = new RpcClientCache();
//        List<Class<?>> list = classList.stream().filter(clazz -> clazz.getAnnotation(RpcApi.class) != null).collect(Collectors.toList());
//        this.cache.initMethodLocator(list);
        this.cache.setClassList(classList);
        log.info("[RPC客户端]{}：缓存初始化完成", name);
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
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 2, 0, 2));
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
                log.info("[RPC客户端]{}：正在链接，主机：{}，端口：{}", name, host, port);
                ChannelFuture channelFuture = bootstrap.connect(host, port);
                this.session = channelFuture.channel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 3秒后检查一下通道是否链接，未链接则重新链接
        RpcScheduler.doTask(() -> {
            if (this.session.isActive()) {
                log.info("[RPC客户端]{}：链接成功，主机：{}，端口：{}", name, host, port);
                connectionSuccessHandler();
            } else {
                log.error("[RPC客户端]{}：链接失败，准备重连，主机：{}，端口：{}", name, host, port);
                // 如果 Socket Channel 未激活，1秒后自动重连
                connection();
            }
        }, clientConfig.getReconnectionTime());
    }

    /**
     * 链接成功后处理一下事情
     */
    private void connectionSuccessHandler() {
        // 初始化Rpc列表
        initRpcTable();
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        int cmd = cache.getCmd(method);
        RpcCallback<?> callback = RpcKit.getRpcCallback(args);
        RpcRequest<?> request = new RpcRequest<>(cmd, args, method.getGenericReturnType(),
                callback, clientConfig.getSerialize());
        return invoke(request);
    }

    public <T> T invokeMethod(String path, Object[] args, Type returnType, boolean async, RpcCallback<T> callback) {
        int cmd = cache.getCmd(path);
        RpcRequest<T> request = new RpcRequest<>(cmd, args, returnType, async, callback, clientConfig.getSerialize());
        return invoke(request);
    }


    /**
     * 真正执行请求的方法
     *
     * @param request 请求对象
     * @param <T>     请求返回值泛型
     * @return 返回对象
     */
    private <T> T invoke(RpcRequest<T> request) {
        long startTime = System.currentTimeMillis();
        RpcInfo<T> rpcInfo = invokeStart(startTime);
        // 记录信息
        rpcInfo.setRequest(request);
        if (!session.isActive()) {
            invokeEnd(rpcInfo, false);
            throw new RpcException("[RPC客户端]" + name + "：初始化失败，Socket Channel未激活，请重新初始化客户端");
        }

        if (request.isAsync()) {
            // 异步调用
            callbackEvents.put(request.getSequence(), rpcInfo);
            // 先将请求信息注册到回调事件上，然后再发送消息，保证服务端消息返回的时候请求信息已经注册了
            this.session.writeAndFlush(request);
            return null;
        }
        // 同步调用
        CountDownLatch latch = new CountDownLatch(1);
        syncLockMap.put(request.getSequence(), latch);
        this.session.writeAndFlush(request);
        try {
            boolean await = latch.await(clientConfig.getSyncTimeout(), TimeUnit.MILLISECONDS);
            if (await) {
                RpcResponse response = responseEvents.remove(request.getSequence());
                // 记录响应数据
                rpcInfo.setResponse(response);
                if (StringUtils.isNotEmpty(response.getException())) {
                    invokeEnd(rpcInfo, false);
                    throw new RpcException("[RPC客户端]" + name + "：RPC响应异常信息：" + response.getException());
                }
                T returnData = response.getReturnData(request.getReturnType());
                invokeEnd(rpcInfo, true);
                return returnData;
            }
        } catch (InterruptedException e) {
            log.error("[RPC客户端]{}同步锁等待发生异常", name);
            e.printStackTrace();
        }
        // 超过了等待时间
        invokeEnd(rpcInfo, false);
        return null;
    }

    /**
     * 检查RPC异步回调信息
     *
     * @param sequence
     * @param response
     */
    private <T> void checkCallback(int sequence, RpcResponse response) {
        RpcInfo<T> rpcInfo = callbackEvents.remove(sequence);
        if (rpcInfo == null) {
            log.error("[RPC客户端]{}：RPC请求未注册回调事件，消息序列号：{}", name, sequence);
            return;
        }
        // 记录响应数据
        rpcInfo.setResponse(response);
        if (StringUtils.isNotEmpty(response.getException())) {
            invokeEnd(rpcInfo, false);
            throw new RpcException("[RPC客户端]" + name + "：RPC服务端响应异常信息：" + response.getException());
        }
        RpcRequest<T> request = rpcInfo.getRequest();
        RpcCallback<T> callback = request.getCallable();
        if (callback == null) {
            log.error("[RPC客户端]{}：异步请求回调函数为空，请求序列号：{}", name, request.getSequence());
            return;
        }
        T returnData = response.getReturnData(request.getReturnType());
        invokeEnd(rpcInfo, true);
        // 异步回调，使用线程池执行 runnable 接口
        callback.execute(returnData);
    }


    private void initRpcSchedule() {
        // 初始化心跳
        initHeartbeat();
        log.info("[RPC客户端]{}：初始化定时任务成功", name);
    }

    private final Queue<Integer> clientHeartbeatQueue = new ConcurrentLinkedQueue<>();

    private <T> void initHeartbeat() {
        RpcScheduler.doLoopTask(() -> {
            checkHeartbeat();
            RpcRequest<T> request = new RpcRequest<>(RpcProtocol.SUB_TYPE_EMPTY, clientConfig.getSerialize());
            clientHeartbeatQueue.offer(request.getSequence());
            // 构造一个消息
            this.session.writeAndFlush(request);
            if (this.clientConfig.isHeartbeatLogEnable()) {
                log.info("[RPC客户端]{}：发送心跳消息：消息序列号：{}", name, request.getSequence());
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
                    log.info("[RPC客户端]{}：确认心跳消息，消息序列号：{}", name, clientHeartbeat);
                }
                return;
            }
        }
    }


    private <T> RpcInfo<T> invokeStart(long startTime) {
        RpcInfo<T> rpcInfo = new RpcInfo<>();
        rpcInfo.setStartTime(startTime);
        return rpcInfo;
    }

    private <T> void invokeEnd(RpcInfo<T> rpcInfo, boolean success) {
        rpcInfo.setEndTime(System.currentTimeMillis());
        rpcInfo.setSuccess(success);
        // 记录，打印日志
        RpcRequest<T> request = rpcInfo.getRequest();
        RpcResponse response = rpcInfo.getResponse();
        if (clientConfig.isMonitorLogEnable()) {
            RpcMethodLocator locator = cache.getMethodLocator(request.getCmd());
            String target = StringUtils.isNotEmpty(locator.getPath()) ? locator.getPath() :
                    locator.getClassName() + "#" + locator.getMethodName() + Arrays.toString(locator.getParamTypeNames());
            log.info("[RPC客户端]{}：执行结果：cmd：{}，目标：{}，是否异步：{}，是否成功，{}，入参：{}，出参：{}，消耗时间：{}ms", name,
                    request.getCmd(), target, request.isAsync(), rpcInfo.isSuccess(), request.getParams(),
                    response == null ? null : response.getReturnData(), rpcInfo.getEndTime() - rpcInfo.getStartTime());
        }
    }


    private static final Map<Integer, Integer> sequenceCmdMap = new ConcurrentHashMap<>();

    private <T> void initRpcTable() {
        RpcRequest<T> request = new RpcRequest<>(RpcProtocol.SUB_TYPE_INTERNAL, clientConfig.getSerialize());
        request.setCmd(InternalCmdEnum.RPC_TABLE.getCmd());
        sequenceCmdMap.put(request.getSequence(), request.getCmd());
        this.session.writeAndFlush(request);
        log.info("[RPC客户端]请求服务端Rpc列表数据，消息序列号：{}，cmd：{}", request.getSequence(), request.getCmd());
    }

    public void handleInternal(int sequence, RpcResponse response) {
        Integer cmd = sequenceCmdMap.get(sequence);
        InternalCmdHandler.handleClient(this, cmd, response);
    }


}
