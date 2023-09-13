package com.herenpeng.rpc.internal;

import com.herenpeng.rpc.client.RpcServerProxy;
import com.herenpeng.rpc.kit.ClassScanner;
import com.herenpeng.rpc.protocol.content.RpcRequest;
import com.herenpeng.rpc.protocol.content.RpcResponse;
import com.herenpeng.rpc.server.RpcServer;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class InternalCmdHandler {
    private static final Map<Integer, InternalCmdHandler> internalCmdMap = new HashMap<>();

    abstract InternalCmdEnum getCmdEnum();

    static {
        try {
            ClassScanner classScanner = new ClassScanner(InternalCmdHandler.class.getPackageName());
            for (Class<?> clazz : classScanner.listClass()) {
                if (clazz != InternalCmdHandler.class && InternalCmdHandler.class.isAssignableFrom(clazz)) {
                    InternalCmdHandler handler = (InternalCmdHandler) clazz.getDeclaredConstructor().newInstance();
                    internalCmdMap.put(handler.getCmdEnum().getCmd(), handler);
                }
            }
        } catch (Exception e) {
            log.error("[RPC服务端]初始化内部命令处理器错误，{}", e.getMessage());
        }
    }


    public static RpcResponse invoke(RpcServer rpcServer, RpcRequest<?> request) {
        InternalCmdHandler internalCmd = internalCmdMap.get(request.getCmd());
        RpcResponse response = new RpcResponse(request.getSubType(), request.getSequence(), request.getSerialize());
        internalCmd.invoke(rpcServer, request, response);
        return response;
    }

    abstract void invoke(RpcServer rpcServer, RpcRequest<?> request, RpcResponse response);


    public static void handleClient(RpcServerProxy rpcServerProxy, int cmd, RpcResponse response) {
        InternalCmdHandler internalCmd = internalCmdMap.get(cmd);
        internalCmd.handleClient(rpcServerProxy, response);
    }

    abstract void handleClient(RpcServerProxy rpcServerProxy, RpcResponse response);

}
