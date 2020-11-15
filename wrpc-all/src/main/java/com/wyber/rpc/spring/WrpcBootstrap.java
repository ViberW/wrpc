package com.wyber.rpc.spring;

import com.wyber.rpc.common.tools.SpiUtils;
import com.wyber.rpc.config.ProtocolConfig;
import com.wyber.rpc.config.ReferenceConfig;
import com.wyber.rpc.config.RegistryConfig;
import com.wyber.rpc.config.ServiceConfig;
import com.wyber.rpc.registry.RegistryService;
import com.wyber.rpc.rpc.Invoker;
import com.wyber.rpc.rpc.cluster.ClusterInvoker;
import com.wyber.rpc.rpc.protocol.Protocol;
import com.wyber.rpc.rpc.protocol.wrpc.WrpcClientInvoker;
import com.wyber.rpc.rpc.proxy.ProxyFactory;

import java.net.NetworkInterface;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Viber
 * @version 1.0
 * @description:
 * @date 2020/11/10 16:02
 */
public class WrpcBootstrap {

    public static void export(ServiceConfig serviceConfig) {
        //此时invoker是根据ref代理;
        Invoker invoker = ProxyFactory.getInvoker(serviceConfig.getReference(), serviceConfig.getService());
        try {
            //多个协议
            for (ProtocolConfig protocolConfig : serviceConfig.getProtocolConfigs()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(protocolConfig.getName() + "://");
                // 此处可选择具体网卡设备 -
                String hostAddress = NetworkInterface.getNetworkInterfaces().
                        nextElement().getInterfaceAddresses().get(0).getAddress().getHostAddress();
                stringBuilder.append(hostAddress + ":")
                        .append(protocolConfig.getPort() + "/")
                        .append(serviceConfig.getService().getName() + "?")
                        .append("transporter=" + protocolConfig.getTransporter())
                        .append("&serialization=" + protocolConfig.getSerialization());

                URI exportUri = new URI(stringBuilder.toString());
                System.out.println("暴露的uri:" + exportUri.toString());
                Protocol protocol = (Protocol) SpiUtils.getServiceImpl(protocolConfig.getName(), Protocol.class);
                protocol.export(exportUri, invoker);

                //注册中心
                for (RegistryConfig registryConfig : serviceConfig.getRegistryConfigs()) {
                    URI registryUri = new URI(registryConfig.getAddress());
                    RegistryService registryService = (RegistryService)
                            SpiUtils.getServiceImpl(registryUri.getScheme(), RegistryService.class);
                    registryService.init(registryUri);
                    registryService.register(exportUri);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object refer(ReferenceConfig referenceConfig) {
        try {
             /*Protocol protocol = (Protocol) SpiUtils.getServiceImpl(
                referenceConfig.getProtocolConfigs().get(0).getName(), Protocol.class);
        Invoker invoker = protocol.refer(new URI("WrpcProtocol://127.0.0.1:10088/com.wyber.rpc.sms.spi.SmsService?transporter=Netty4Transporter&serialization=JsonSerialization"));
        */
            //演变为clusterInvoker
            Invoker invoker = new ClusterInvoker(referenceConfig);
            return ProxyFactory.getProxy(invoker, new Class[]{referenceConfig.getService()});
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
