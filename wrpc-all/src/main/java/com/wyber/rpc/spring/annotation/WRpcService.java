package com.wyber.rpc.spring.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记这个类要成为一个服务提供者
 */
@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface WRpcService {
    /**
     * 如果有多个接口，需要自己指定一个
     */
    Class<?> interfaceClass() default void.class;

}
