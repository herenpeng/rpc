package com.herenpeng.rpc.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

/**
 * @author herenpeng
 * @since 2023-02-09 21:53
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RpcServerConfig implements Serializable {

    /**
     * 服务端名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * 服务端启动端口
     */
    @JsonProperty("port")
    private int port = 10000;

    /**
     * 是否启用心跳日志
     */
    @JsonProperty("heartbeat-log-enable")
    private boolean heartbeatLogEnable = false;

    /**
     * 工作线程数，如果值为0，则获取（机器CPU核心数 * 2）作为线程数
     */
    @JsonProperty("worker-thread-num")
    private int workerThreadNum = 0;

}
