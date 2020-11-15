package com.wyber.rpc;

import com.wyber.rpc.order.spi.OrderService;
import com.wyber.rpc.spring.annotation.EnableWRPC;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author Viber
 * @version 1.0
 * @description:
 * @date 2020/11/10 9:52
 */
@Configuration
@ComponentScan("com.wyber.rpc")
@PropertySource("classpath:/wrpc.properties")
@EnableWRPC
public class OrderApplication {

    public static void main(String[] args) throws IOException {
        final AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(OrderApplication.class);
        context.start();

        final CyclicBarrier cyclicBarrier = new CyclicBarrier(10);
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        cyclicBarrier.await();
                        // 测试..模拟调用接口 -- 一定是远程，因为当前的系统没有具体实现类
                        OrderService orderService = context.getBean(OrderService.class);
                        orderService.create("买一瓶水");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }


        // 阻塞不退出
        System.in.read();
        context.close();
    }
}
