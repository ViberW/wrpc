package com.wyber.rpc.registry;

import com.wyber.rpc.registry.zookeeper.ZookeeperRegistry;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

/**
 * @author Viber
 * @version 1.0
 * @description:
 * @date 2020/11/11 14:51
 */
public class ZookeeperRegistryTest {

    public static void main(String[] args) throws URISyntaxException, IOException {
        RegistryService registry = new ZookeeperRegistry();
        registry.init(new URI("ZookeeperRegistry://127.0.0.1:2181"));
        registry.subscribe("com.wyber.rpc.sms.spi.SmsService", new NotifyListener() {
            @Override
            public void notify(Set<URI> uris) {
                System.out.println("当前的:" + uris);
            }
        });

        System.in.read();
    }
}
