package com.herenpeng.rpc.proto;

import com.herenpeng.rpc.common.RpcMethodLocator;
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
public class RpcReq implements Serializable {

    private RpcMethodLocator methodLocator;

    private Object[] params;

}
