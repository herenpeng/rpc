package com.herenpeng.rpc.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * @author herenpeng
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RpcClientConfig extends RpcConfig {

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
     *  默认的心跳时间，默认10000毫秒
     */
    @JsonProperty("heartbeat-time")
    private long heartbeatTime = 10000;

    /**
     * 心跳失效触发次数，默认3次
     */
    @JsonProperty("heartbeat-invalid-times")
    private int heartbeatInvalidTimes = 3;

    @JsonProperty("heartbeat-log-enable")
    private boolean heartbeatLogEnable = false;

}
