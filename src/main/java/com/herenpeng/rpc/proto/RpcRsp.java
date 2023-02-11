package com.herenpeng.rpc.proto;

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
public class RpcRsp<T> implements Serializable {

    private T returnData;

    private String exception;

}
