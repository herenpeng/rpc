package com.herenpeng.rpc.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.herenpeng.rpc.kit.RpcCallback;
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

    private RpcMethodLocator methodLocator;

    private long startTime;

    private long endTime;

    private boolean success;

    private Object[] params;

    private Object returnData;

    @JsonIgnore
    private Class<T> returnType;

    @JsonIgnore
    private RpcCallback<T> callable;


}
