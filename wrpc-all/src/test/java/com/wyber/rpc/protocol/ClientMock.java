package com.wyber.rpc.protocol;

import com.wyber.rpc.common.serialize.Serialization;
import com.wyber.rpc.common.tools.ByteUtil;
import com.wyber.rpc.common.tools.SpiUtils;
import com.wyber.rpc.rpc.RpcInvocation;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Viber
 * @version 1.0
 * @description:
 * @date 2020/11/10 13:50
 */
public class ClientMock {
    public static void main(String[] args) throws Exception {
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setServiceName("com.wyber.rpc.sms.spi.SmsService");
        rpcInvocation.setMethodName("send");
        //String phone, String content
        rpcInvocation.setParameterTypes(new Class[]{String.class, String.class});
        rpcInvocation.setArguments(new Object[]{"10086", "短信"});

        Serialization serialization =
                (Serialization) SpiUtils.getServiceImpl("JsonSerialization", Serialization.class);
        byte[] requestBody = serialization.serialize(rpcInvocation);

        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeByte(0xda);
        byteBuf.writeByte(0xbb);
        byteBuf.writeBytes(ByteUtil.int2bytes(requestBody.length));
        byteBuf.writeBytes(requestBody);

        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("127.0.0.1", 10088));
        sc.write(ByteBuffer.wrap(byteBuf.array()));

        ByteBuffer response = ByteBuffer.allocate(1025);
        sc.read(response);
        System.out.println("响应内容:" + new String(response.array()));
    }
}
