package com.herenpeng.rpc.annotation;

import java.lang.annotation.*;

/**
 * @author herenpeng
 * @since 2023-02-19 16:42
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcMethod {

    String value() default "";

}
