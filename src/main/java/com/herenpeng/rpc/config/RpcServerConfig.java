package com.herenpeng.rpc.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * @author herenpeng
 * @since 2023-02-09 21:53
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RpcServerConfig {

    /**
     * 是否启用心跳日志
     */
    @JsonProperty("heartbeat-log-enable")
    private boolean heartbeatLogEnable = false;

}
