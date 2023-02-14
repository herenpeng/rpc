package com.herenpeng.rpc.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.herenpeng.rpc.client.RpcClient;
import com.herenpeng.rpc.kit.RpcCallback;
import lombok.*;

import java.util.concurrent.Callable;

/**
 * @author herenpeng
 * @since 2023-02-14 20:26
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RpcInfo {

    private RpcMethodLocator methodLocator;

    private long startTime;

    private long endTime;

    private boolean success;

    @JsonIgnore
    private RpcCallback callable;



}
