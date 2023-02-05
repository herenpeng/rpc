package com.herenpeng.rpc.annotation;

import java.lang.annotation.*;

/**
 * @author herenpeng
 * @since 2023-02-05 22:55
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcService {
}
