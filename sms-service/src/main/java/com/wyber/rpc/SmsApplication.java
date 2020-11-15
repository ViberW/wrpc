package com.wyber.rpc;

import com.wyber.rpc.spring.annotation.EnableWRPC;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.wyber.rpc") // spring注解扫描
@PropertySource("classpath:/wrpc.properties")
@EnableWRPC
public class SmsApplication {
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SmsApplication.class);
        context.start();

        // SmsServiceImpl smsService = context.getBean(SmsServiceImpl.class);
        // System.out.println(smsService.send("10086","启动时测试一条短信"));

        // 阻塞不退出
        System.in.read();
        context.close();
    }
}