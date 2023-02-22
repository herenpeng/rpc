package com.herenpeng.rpc.config;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author herenpeng
 * @since 2023-02-09 20:46
 */
public class RpcConfigTest {

    @Test
    public void configTest() {
        RpcConfigProcessor processor = new RpcConfigProcessor();
        RpcConfig rpc = processor.getRpc();
        Assert.assertNotNull(rpc);

        RpcClientConfig client = rpc.getClient();
        Assert.assertNotNull(client);

        RpcServerConfig server = rpc.getServer();
        Assert.assertNotNull(server);
    }

}
