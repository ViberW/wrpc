package com.wyber.rpc.spring.annotation;

import com.wyber.rpc.spring.WRPCConfiguration;
import com.wyber.rpc.spring.WRPCPostProcessor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Viber
 * @version 1.0
 * @description: 开启rpc
 * @date 2020/11/10 10:11
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({WRPCConfiguration.class, WRPCPostProcessor.class})
public @interface EnableWRPC {
}
