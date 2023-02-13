package com.herenpeng.rpc.config;

/**
 * @author herenpeng
 * @since 2023-02-09 20:46
 */
public class RpcConfigTest {

    public static void main(String[] args) {
        RpcConfigProcessor processor = new RpcConfigProcessor();
        RpcConfig rpc = processor.getRpc();
        RpcClientConfig client = rpc.getClient();
        System.out.println(client);
        RpcServerConfig server = rpc.getServer();
        System.out.println(server);

    }

}
