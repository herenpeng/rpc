package com.herenpeng.rpc.common;

import com.herenpeng.rpc.protocol.content.RpcRequest;
import com.herenpeng.rpc.protocol.content.RpcResponse;
import lombok.*;

import java.io.Serializable;

/**
 * @author herenpeng
 * @since 2023-02-14 20:26
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RpcInfo<T> implements Serializable {

    @NonNull
    private RpcRequest<T> request;

    private RpcResponse response;

    private long startTime;

    private long endTime;

    private boolean success;


}
