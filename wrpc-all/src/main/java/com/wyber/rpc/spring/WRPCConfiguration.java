package com.wyber.rpc.spring;

import com.wyber.rpc.config.ProtocolConfig;
import com.wyber.rpc.config.RegistryConfig;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.reflect.Field;

/**
 * @author Viber
 * @version 1.0
 * @description: 加载protocol和register配置, 并注入到spring中
 * @date 2020/11/10 10:14
 */
public class WRPCConfiguration implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    StandardEnvironment environment;

    public void setEnvironment(Environment environment) {
        this.environment = (StandardEnvironment) environment;
    }

    /**
     * 帮助加载protocol和register配置
     *
     * @param importingClassMetadata
     * @param registry
     * @param importBeanNameGenerator
     */
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry,
                                        BeanNameGenerator importBeanNameGenerator) {
        //protocolConfig
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(ProtocolConfig.class);
        for (Field field : ProtocolConfig.class.getDeclaredFields()) {
            String value = environment.getProperty("wrpc.protocol." + field.getName());//获取所有前缀数据
            beanDefinitionBuilder.addPropertyValue(field.getName(), value);
        }
        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        String beanName = importBeanNameGenerator.generateBeanName(beanDefinition, registry);
        System.out.println("ProtocolConfig beanName:" + beanName);
        registry.registerBeanDefinition(beanName, beanDefinition);

        //registryConfig
        beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(RegistryConfig.class);
        for (Field field : RegistryConfig.class.getDeclaredFields()) {
            String value = environment.getProperty("wrpc.registry." + field.getName());//获取所有前缀数据
            beanDefinitionBuilder.addPropertyValue(field.getName(), value);
        }
        beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        beanName = importBeanNameGenerator.generateBeanName(beanDefinition, registry);
        System.out.println("RegistryConfig beanName:" + beanName);
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

}
