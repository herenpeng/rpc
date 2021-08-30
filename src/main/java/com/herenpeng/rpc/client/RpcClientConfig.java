package com.herenpeng.rpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author herenpeng
 */
public class RpcClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientConfig.class);

    // 同步调用超时时长，默认 3 秒
    private long syncTimeout;

    // 默认重连时间间隔，默认 3 秒
    private long reconnectionTime;

    // 默认的心跳时间，默认 10 秒
    private long heartbeatTime;

    // 心跳失效触发次数，默认 3 次
    private int heartbeatInvalidTimes;

    // RPC 配置文件名称
    private static final String rpcConfigName = "rpc.properties";
    private static final String rpcConfigClientPrefix = "rpc.client.";
    private static final Properties properties = new Properties();

    // 配置文件优先级：代码配置 > rpc.properties > 默认配置
    public RpcClientConfig() {
        try {
            InputStream inputStream = RpcClientConfig.class.getClassLoader().getResourceAsStream(rpcConfigName);
            if (inputStream != null) {
                properties.load(inputStream);
            }
            this.syncTimeout = Long.parseLong(properties.getProperty(rpcConfigClientPrefix + "connection.sync-timeout", "3000"));
            this.reconnectionTime = Long.parseLong(properties.getProperty(rpcConfigClientPrefix + "connection.reconnection-time", "3000"));
            this.heartbeatTime = Long.parseLong(properties.getProperty(rpcConfigClientPrefix + "heartbeat.time", "10000"));
            this.heartbeatInvalidTimes = Integer.parseInt(properties.getProperty(rpcConfigClientPrefix + "heartbeat.invalid-times", "3"));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[RPC客户端]配置解析失败");
        }
    }


    public long getSyncTimeout() {
        return syncTimeout;
    }

    public void setSyncTimeout(long syncTimeout) {
        this.syncTimeout = syncTimeout;
    }

    public long getReconnectionTime() {
        return reconnectionTime;
    }

    public void setReconnectionTime(long reconnectionTime) {
        this.reconnectionTime = reconnectionTime;
    }

    public long getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(long heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

    public int getHeartbeatInvalidTimes() {
        return heartbeatInvalidTimes;
    }

    public void setHeartbeatInvalidTimes(int heartbeatInvalidTimes) {
        this.heartbeatInvalidTimes = heartbeatInvalidTimes;
    }

    @Override
    public String toString() {
        return "RpcClientConfig{" +
                "syncTimeout=" + syncTimeout +
                ", reconnectionTime=" + reconnectionTime +
                ", heartbeatTime=" + heartbeatTime +
                ", heartbeatInvalidTimes=" + heartbeatInvalidTimes +
                '}';
    }

}
