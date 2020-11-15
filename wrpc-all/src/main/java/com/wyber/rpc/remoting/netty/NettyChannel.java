package com.wyber.rpc.remoting.netty;

import com.wyber.rpc.remoting.WrpcChannel;
import io.netty.channel.Channel;

public class NettyChannel implements WrpcChannel {

    Channel channel;

    public NettyChannel(Channel channel) {
        this.channel = channel;
    }

    public void send(byte[] message) {
        channel.writeAndFlush(message);
    }
}
