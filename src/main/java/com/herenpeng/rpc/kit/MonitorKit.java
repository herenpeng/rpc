package com.herenpeng.rpc.kit;

import com.herenpeng.rpc.client.RpcClient;
import com.herenpeng.rpc.client.RpcServerProxy;
import com.herenpeng.rpc.config.RpcClientConfig;
import com.herenpeng.rpc.internal.InternalCmdEnum;

public class MonitorKit {

    private static final String CLIENT_NAME = "Monitor";

    public static void monitor(String host, int port) {
        RpcClient rpcClient = new RpcClient();
        RpcClientConfig client = new RpcClientConfig();
        client.setName(CLIENT_NAME);
        client.setHost(host);
        client.setPort(port);
        rpcClient.start(null, client, null);
        RpcServerProxy rpcServerProxy = RpcClient.get(CLIENT_NAME);
        rpcServerProxy.invokeInternal(InternalCmdEnum.MONITOR);

    }

}
