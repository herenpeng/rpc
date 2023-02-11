package com.herenpeng.rpc.common;

import lombok.*;

/**
 * @author herenpeng
 * @since 2023-02-11 11:53
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RpcMethodLocator {

    private String className;

    private String methodName;

    private String[] paramTypeNames;

    /**
     * 异步
     */
    private boolean async;

}
