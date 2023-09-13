package com.herenpeng.rpc.monitor;

import com.herenpeng.rpc.client.RpcClient;
import com.herenpeng.rpc.config.RpcClientConfig;

import java.util.Collections;

public class Monitor {

    private static final String MONITOR = "Monitor";

    public static void monitor(String host, int port) {
        RpcClient rpcClient = new RpcClient();
        RpcClientConfig clientConfig = new RpcClientConfig();
        clientConfig.setName(MONITOR);
        clientConfig.setHost(host);
        clientConfig.setPort(port);
        rpcClient.start(Monitor.class, clientConfig, Collections.singletonList(Monitor.class));
    }

}
