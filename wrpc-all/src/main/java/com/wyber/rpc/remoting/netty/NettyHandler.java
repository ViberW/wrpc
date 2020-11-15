package com.wyber.rpc.remoting.netty;

import com.wyber.rpc.remoting.Handler;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Viber
 * @version 1.0
 * @description:
 * @date 2020/11/10 10:45
 */
public class NettyHandler extends ChannelDuplexHandler {
    Handler handler;

    public NettyHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        handler.onReceive(new NettyChannel(ctx.channel()), msg);
    }
}
