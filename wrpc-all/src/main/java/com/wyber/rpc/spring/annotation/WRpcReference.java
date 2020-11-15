package com.wyber.rpc.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注入一个rpc远程引用
 *
 * @author Tony
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WRpcReference {
    String loadbalance() default "RandomLoadBalance";
}
