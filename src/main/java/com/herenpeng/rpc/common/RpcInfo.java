package com.herenpeng.rpc.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.herenpeng.rpc.kit.RpcCallback;
import com.herenpeng.rpc.proto.content.RpcRequest;
import com.herenpeng.rpc.proto.content.RpcResponse;
import lombok.*;

/**
 * @author herenpeng
 * @since 2023-02-14 20:26
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RpcInfo<T> {

    @NonNull
    private RpcRequest<T> request;

    private RpcResponse response;

    private long startTime;

    private long endTime;

    private boolean success;


}
