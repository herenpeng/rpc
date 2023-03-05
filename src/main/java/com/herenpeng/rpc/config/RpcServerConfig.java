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

}
