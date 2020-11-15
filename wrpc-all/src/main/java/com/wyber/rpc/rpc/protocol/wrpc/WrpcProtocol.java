package com.wyber.rpc.rpc.protocol.wrpc;

import com.wyber.rpc.common.serialize.Serialization;
import com.wyber.rpc.common.tools.SpiUtils;
import com.wyber.rpc.common.tools.URIUtils;
import com.wyber.rpc.remoting.Client;
import com.wyber.rpc.remoting.Transporter;
import com.wyber.rpc.rpc.Invoker;
import com.wyber.rpc.rpc.Response;
import com.wyber.rpc.rpc.RpcInvocation;
import com.wyber.rpc.rpc.protocol.Protocol;
import com.wyber.rpc.rpc.protocol.wrpc.codec.WrpcCodec;
import com.wyber.rpc.rpc.protocol.wrpc.handler.WrpcClientHandler;
import com.wyber.rpc.rpc.protocol.wrpc.handler.WrpcServerHandler;

import java.net.URI;

/**
 * @author Viber
 * @version 1.0
 * @description:
 * @date 2020/11/10 11:41
 */
public class WrpcProtocol implements Protocol {

    //开始暴露服务
    public void export(URI exportUri, Invoker invoker) {
        WrpcCodec wrpcCodec = new WrpcCodec();
        wrpcCodec.setDecodeType(RpcInvocation.class);

        String serialize = URIUtils.getParam(exportUri, "serialization");
        Serialization serialization = (Serialization) SpiUtils.getServiceImpl(serialize, Serialization.class);
        wrpcCodec.setSerialization(serialization);

        WrpcServerHandler wrpcServerHandler = new WrpcServerHandler();
        wrpcServerHandler.setInvoker(invoker);
        wrpcServerHandler.setSerialization(serialization);

        String transporter = URIUtils.getParam(exportUri, "transporter");
        Transporter trans = (Transporter) SpiUtils.getServiceImpl(transporter, Transporter.class);
        trans.start(exportUri, wrpcCodec, wrpcServerHandler);
    }

    @Override
    public Invoker refer(URI consumerUri) {
        WrpcCodec wrpcCodec = new WrpcCodec();
        wrpcCodec.setDecodeType(Response.class);

        String serialize = URIUtils.getParam(consumerUri, "serialization");
        Serialization serialization = (Serialization) SpiUtils.getServiceImpl(serialize, Serialization.class);
        wrpcCodec.setSerialization(serialization);

        WrpcClientHandler wrpcClientHandler = new WrpcClientHandler();

        String transporter = URIUtils.getParam(consumerUri, "transporter");
        Transporter trans = (Transporter) SpiUtils.getServiceImpl(transporter, Transporter.class);
        Client client = trans.connect(consumerUri, wrpcCodec, wrpcClientHandler);

        //构建WrpcClientInvoker
        WrpcClientInvoker invoker = new WrpcClientInvoker(client, serialization);

        return invoker;
    }

}
