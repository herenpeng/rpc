package com.herenpeng.rpc.annotation;

import java.lang.annotation.*;

/**
 * RPC的API接口注解
 *
 * @author herenpeng
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcApi {
}
