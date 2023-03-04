package com.herenpeng.rpc;

import com.herenpeng.rpc.annotation.RpcApplication;
import com.herenpeng.rpc.server.RpcServer;

/**
 * @author herenpeng
 * @since 2021-08-30 23:05
 */
@RpcApplication
public class MockRpcServer {

    public static void main(String[] args) {

        RpcServer rpcServer = new RpcServer();
        rpcServer.start(MockRpcServer.class);
    }

}
