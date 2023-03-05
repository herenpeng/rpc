package com.herenpeng.rpc.config;

import org.junit.Test;

/**
 * @author herenpeng
 * @since 2023-02-09 20:46
 */
public class RpcConfigTest {

    @Test
    public void configTest() {
        RpcConfigProcessor processor = new RpcConfigProcessor("rpc.yaml");
        RpcConfig rpc = processor.getRpc();
        System.out.println(rpc);

        System.out.println(rpc.getClient());

        System.out.println(rpc.getServer());
    }

}
