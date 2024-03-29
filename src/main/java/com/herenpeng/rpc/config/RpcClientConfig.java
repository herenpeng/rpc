package com.herenpeng.rpc.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.herenpeng.rpc.kit.serialize.Serializer;
import lombok.*;

import java.io.Serializable;

/**
 * @author herenpeng
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RpcClientConfig implements Serializable {

    /**
     * 客户端名称
     */
    private String name;

    /**
     * 远端服务主机地址
     */
    private String host;

    /**
     * 远端服务端口
     */
    private int port;

    /**
     * 同步调用超时时长，默认1000毫秒
     */
    @JsonProperty("sync-timeout")
    private long syncTimeout = 1000;

    /**
     * 默认重连时间间隔，默认3000毫秒
     */
    @JsonProperty("reconnection-time")
    private long reconnectionTime = 3000;

    /**
     * 默认的心跳时间，默认10000毫秒
     */
    @JsonProperty("heartbeat-time")
    private long heartbeatTime = 10000;

    /**
     * 心跳失效触发次数，默认3次
     */
    @JsonProperty("heartbeat-invalid-times")
    private int heartbeatInvalidTimes = 3;

    /**
     * 序列化方式
     */
    @JsonProperty("serialize")
    private byte serialize = Serializer.JSON;

    /**
     * 开启客户端心跳日志
     */
    @JsonProperty("heartbeat-log-enable")
    private boolean heartbeatLogEnable = false;

    /**
     * 开启客户端性能监控日志
     */
    @JsonProperty("monitor-log-enable")
    private boolean monitorLogEnable = false;

    /**
     * 数据压缩启动要求数据大小，单位：字节长度
     */
    @JsonProperty("compress-enable-size")
    private int compressEnableSize = 1024 * 10;

}
