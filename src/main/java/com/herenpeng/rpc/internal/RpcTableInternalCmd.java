package com.herenpeng.rpc.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.herenpeng.rpc.client.RpcClientCache;
import com.herenpeng.rpc.client.RpcServerProxy;
import com.herenpeng.rpc.common.RpcMethodLocator;
import com.herenpeng.rpc.protocol.content.RpcRequest;
import com.herenpeng.rpc.protocol.content.RpcResponse;
import com.herenpeng.rpc.server.RpcServer;
import com.herenpeng.rpc.server.RpcServerCache;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.Map;

@Slf4j
public class RpcTableInternalCmd extends InternalCmdHandler {

    @Override
    InternalCmdEnum getCmdEnum() {
        return InternalCmdEnum.RPC_TABLE;
    }

    @Override
    Object invoke(RpcServer rpcServer) {
        RpcServerCache cache = rpcServer.getCache();
        return cache.getRpcTable();
    }

    @Override
    void handleClient(RpcServerProxy rpcServerProxy, RpcResponse response) {
        Type returnType = new TypeReference<Map<Integer, RpcMethodLocator>>() {
        }.getType();
        Map<Integer, RpcMethodLocator> rpcTable = response.getReturnData(returnType);
        RpcClientCache cache = rpcServerProxy.getCache();
        cache.initMethodCmd(rpcTable);
        log.info("[RPC客户端]获取Rpc列表信息，{}", rpcTable);
    }

}
