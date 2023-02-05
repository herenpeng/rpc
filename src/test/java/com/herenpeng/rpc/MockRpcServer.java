package com.herenpeng.rpc;

import com.herenpeng.rpc.server.RpcServer;

/**
 * @author herenpeng
 * @since 2021-08-30 23:05
 */
public class MockRpcServer {

    public static void main(String[] args) {

        RpcServer rpcServer = new RpcServer();
        rpcServer.start(10000, MockRpcServer.class);
    }

}
