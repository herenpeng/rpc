package com.herenpeng.rpc.internal;

import com.herenpeng.rpc.client.RpcServerProxy;
import com.herenpeng.rpc.protocol.content.RpcRequest;
import com.herenpeng.rpc.protocol.content.RpcResponse;
import com.herenpeng.rpc.server.RpcServer;

public class MonitorInternalCmd extends InternalCmdHandler {

    @Override
    InternalCmdEnum getCmdEnum() {
        return InternalCmdEnum.MONITOR;
    }

    @Override
    void invoke(RpcServer rpcServer, RpcRequest<?> request, RpcResponse response) {

    }

    @Override
    void handleClient(RpcServerProxy rpcServerProxy, RpcResponse response) {

    }

}
