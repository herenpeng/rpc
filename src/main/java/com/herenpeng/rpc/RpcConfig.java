package com.herenpeng.rpc;

import com.herenpeng.rpc.client.RpcClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author herenpeng
 * @since 2021-09-07 23:08
 */
public class RpcConfig {

    private static final Logger logger = LoggerFactory.getLogger(RpcConfig.class);

    // RPC 配置文件名称
    private static final String rpcConfigName = "rpc.properties";
    protected static final String rpcClientConfigPrefix = "rpc.client.";
    protected static final String rpcServerConfigPrefix = "rpc.server.";
    protected static final Properties properties = new Properties();

    // 配置文件优先级：代码配置 > rpc.properties > 默认配置
    static {
        try {
            InputStream inputStream = RpcClientConfig.class.getClassLoader().getResourceAsStream(rpcConfigName);
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[RPC客户端]配置解析失败");
        }
    }

    protected long getLong(String prefix, String name, long defaultValue) {
        String value = properties.getProperty(prefix + name);
        return value == null ? defaultValue : Long.parseLong(value);
    }

    protected int getInt(String prefix, String name, int defaultValue) {
        String value = properties.getProperty(prefix + name);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    protected boolean getInt(String prefix, String name, boolean defaultValue) {
        String value = properties.getProperty(prefix + name);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

}
