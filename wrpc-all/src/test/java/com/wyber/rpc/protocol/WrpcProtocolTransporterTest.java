package com.wyber.rpc.protocol;

import com.wyber.rpc.common.serialize.json.JsonSerialization;
import com.wyber.rpc.remoting.netty.Netty4Transporter;
import com.wyber.rpc.rpc.RpcInvocation;
import com.wyber.rpc.rpc.protocol.wrpc.codec.WrpcCodec;
import com.wyber.rpc.rpc.protocol.wrpc.handler.WrpcServerHandler;

import java.net.URI;
import java.net.URISyntaxException;

// 集成了trpc 一套协议处理机制
public class WrpcProtocolTransporterTest {
    public static void main(String[] args) throws URISyntaxException {
        WrpcCodec trpcCodec = new WrpcCodec();
        trpcCodec.setDecodeType(RpcInvocation.class);
        trpcCodec.setSerialization(new JsonSerialization());

        WrpcServerHandler trpcServerHandler = new WrpcServerHandler();
        new Netty4Transporter().start(new URI("TRPP://127.0.0.1:8080"),
                trpcCodec, trpcServerHandler);
    }
}
