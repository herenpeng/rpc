package com.herenpeng.rpc.annotation;

import java.lang.annotation.*;

/**
 * @author herenpeng
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcClientApi {
}
