package com.wyber.rpc.spring;

import com.wyber.rpc.common.tools.SpiUtils;
import com.wyber.rpc.config.ProtocolConfig;
import com.wyber.rpc.config.ReferenceConfig;
import com.wyber.rpc.config.RegistryConfig;
import com.wyber.rpc.config.ServiceConfig;
import com.wyber.rpc.remoting.*;
import com.wyber.rpc.rpc.protocol.Protocol;
import com.wyber.rpc.spring.annotation.WRpcReference;
import com.wyber.rpc.spring.annotation.WRpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.List;

/**
 * @author Viber
 * @version 1.0
 * @description: 对于特定的注解进行初始化
 * @date 2020/11/10 10:15
 */
public class WRPCPostProcessor implements ApplicationContextAware, InstantiationAwareBeanPostProcessor {
    ApplicationContext context;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    /**
     * 此时对象已经创建了,需要针对属性进行设置,可以查看是否有WRpcReference和WRpcService的注解
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //服务提供者
        if (bean.getClass().isAnnotationPresent(WRpcService.class)) {
            System.out.println(bean.getClass().getSimpleName() + "准备WRpcService");

            ServiceConfig serviceConfig = new ServiceConfig();
            serviceConfig.addProtocolConfig(context.getBean(ProtocolConfig.class));
            serviceConfig.addRegistryConfig(context.getBean(RegistryConfig.class));
            serviceConfig.setReference(bean);

            WRpcService tRpcService = bean.getClass().getAnnotation(WRpcService.class);
            if (void.class == tRpcService.interfaceClass()) {
                serviceConfig.setService(bean.getClass().getInterfaces()[0]);
            } else {
                serviceConfig.setService(tRpcService.interfaceClass());
            }
            WrpcBootstrap.export(serviceConfig);
        }

        //服务消费者
        //遍历属性,判断是否存在@WRpcReference注解,若存在,则refer
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(WRpcReference.class)) {
                //说明需要代理
                ReferenceConfig referenceConfig = new ReferenceConfig();
                referenceConfig.addProtocolConfig(context.getBean(ProtocolConfig.class));
                referenceConfig.addRegistryConfig(context.getBean(RegistryConfig.class));
                referenceConfig.setService(field.getType());

                WRpcReference annotation = field.getAnnotation(WRpcReference.class);
                referenceConfig.setLoadbalance(annotation.loadbalance());

                Object obj = WrpcBootstrap.refer(referenceConfig);
                try {
                    field.setAccessible(true);
                    field.set(bean, obj);
                } catch (IllegalAccessException e) {
                }
            }
        }
        return bean;
    }
}
