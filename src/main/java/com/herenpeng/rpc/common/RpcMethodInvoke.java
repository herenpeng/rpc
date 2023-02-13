package com.herenpeng.rpc.common;

import lombok.*;

import java.lang.reflect.Method;

/**
 * @author herenpeng
 * @since 2023-02-13 20:02
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RpcMethodInvoke {

    private Method method;

    private Object rpcServer;

}
