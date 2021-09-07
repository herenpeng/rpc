package com.herenpeng.rpc.client;

import com.herenpeng.rpc.RpcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author herenpeng
 */
public class RpcClientConfig extends RpcConfig {

    // 同步调用超时时长，默认 3 秒
    private long syncTimeout;

    // 默认重连时间间隔，默认 3 秒
    private long reconnectionTime;

    // 默认的心跳时间，默认 10 秒
    private long heartbeatTime;

    // 心跳失效触发次数，默认 3 次
    private int heartbeatInvalidTimes;

    public RpcClientConfig() {
        this.syncTimeout = getLong(rpcClientConfigPrefix, "connection.sync-timeout", 3000L);
        this.reconnectionTime = getLong(rpcClientConfigPrefix, "connection.reconnection-time", 3000L);
        this.heartbeatTime = getLong(rpcClientConfigPrefix, "heartbeat.time", 10000L);
        this.heartbeatInvalidTimes = getInt(rpcClientConfigPrefix, "heartbeat.invalid-times", 3);
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
