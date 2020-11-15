package com.wyber.rpc.rpc.cluster;

import com.wyber.rpc.common.tools.SpiUtils;
import com.wyber.rpc.config.ReferenceConfig;
import com.wyber.rpc.config.RegistryConfig;
import com.wyber.rpc.registry.RegistryService;
import com.wyber.rpc.rpc.Invoker;
import com.wyber.rpc.rpc.RpcInvocation;
import com.wyber.rpc.rpc.protocol.Protocol;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Viber
 * @version 1.0
 * @description:
 * @date 2020/11/12 10:28
 */
public class ClusterInvoker implements Invoker {

    ReferenceConfig referenceConfig;
    String loadBalance;

    Map<URI, Invoker> invokers = new ConcurrentHashMap<>();

    public ClusterInvoker(ReferenceConfig referenceConfig) throws URISyntaxException {
        this.referenceConfig = referenceConfig;
        this.loadBalance = referenceConfig.getLoadbalance();
        String name = getInterface().getName();
        //注册registry, 并订阅数据变更
        for (RegistryConfig registryConfig : referenceConfig.getRegistryConfigs()) {
            URI uri = new URI(registryConfig.getAddress());
            RegistryService registry = (RegistryService) SpiUtils.getServiceImpl(uri.getScheme(), RegistryService.class);
            registry.init(uri);
            registry.subscribe(name, uris -> {
                //删除多余的
                for (URI u : invokers.keySet()) {
                    if (!uris.contains(u)) {
                        invokers.remove(u);
                    }
                }
                //初始化不存在的
                for (URI u : uris) {
                    if (!invokers.containsKey(u)) {
                        Protocol protocol = (Protocol) SpiUtils.getServiceImpl(u.getScheme(), Protocol.class);
                        Invoker invoker = protocol.refer(u);
                        invokers.putIfAbsent(u, invoker);
                    }
                }
            });
        }

    }

    @Override
    public Class getInterface() {
        return referenceConfig.getService();
    }

    @Override
    public Object invoke(RpcInvocation rpcInvocation) throws Exception {
        //负载均衡选择器
        LoadBalance balance = (LoadBalance) SpiUtils.getServiceImpl(loadBalance, LoadBalance.class);
        Invoker invoker = balance.select(invokers);
        return invoker.invoke(rpcInvocation);
    }
}
