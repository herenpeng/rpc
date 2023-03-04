package com.herenpeng.rpc.annotation;

import java.lang.annotation.*;

/**
 * @author herenpeng
 * @since 2023-03-04 12:30
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RpcApplication {

    String configFile() default "rpc.yaml";

}
