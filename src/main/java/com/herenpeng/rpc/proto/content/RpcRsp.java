package com.herenpeng.rpc.proto.content;

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
public class RpcRsp implements Serializable {

    private Object returnData;

    private String exception;

}
