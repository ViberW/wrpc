package com.wyber.rpc.remoting.netty;

import com.wyber.rpc.remoting.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.List;

/**
 * @author Viber
 * @version 1.0
 * @description: 对数据进行编解码操作
 * @date 2020/11/10 13:08
 */
public class NettyCodec extends ChannelDuplexHandler {

    Codec codec;

    public NettyCodec(Codec codec) {
        this.codec = codec;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;
        byte[] data = new byte[buffer.readableBytes()];
        buffer.readBytes(data);
        //粘包+拆包
        List<Object> decode = codec.decode(data);
        for (Object o : decode) {
            ctx.fireChannelRead(o);
        }
        System.out.println("编解码数据内容:" + msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        //最终发送数据,  server端-response  client端-rpcInvocation
        byte[] data = codec.encode(msg);
        //必须要转化为byteBuf
        super.write(ctx, Unpooled.wrappedBuffer(data), promise);
    }
}
