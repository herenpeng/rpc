package com.herenpeng.rpc.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author herenpeng
 * @since 2023-06-13 20:11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RpcServerPerformanceData implements Serializable {

    /**
     * 服务启动时间（毫秒）
     */
    private long startTime;
    /**
     * 启动耗时（毫秒）
     */
    private long startSuccessTime;

    private int requestNum;
    private int successNum;
    private int failNum;
    private int exceptionNum;

}
