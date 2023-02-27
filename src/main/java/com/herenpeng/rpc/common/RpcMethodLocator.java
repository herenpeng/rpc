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

    /**
     * 最后一个参数如果为rpc回调（async值为true），则会删掉最后一个参数
     */
    private String[] paramTypeNames;
    /**
     * 异步方法标识
     */
    private boolean async;

}
