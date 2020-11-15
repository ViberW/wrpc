package com.wyber.rpc.remoting.netty;

import com.wyber.rpc.remoting.Client;
import com.wyber.rpc.remoting.Codec;
import com.wyber.rpc.remoting.Handler;
import com.wyber.rpc.remoting.WrpcChannel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.URI;

/**
 * @author Viber
 * @version 1.0
 * @description:
 * @date 2020/11/12 9:55
 */
public class NettyClient implements Client {

    NioEventLoopGroup group = new NioEventLoopGroup();
    WrpcChannel channel;

    @Override
    public void connect(URI uri, Codec codec, Handler handler) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyCodec(codec.createInstance()));
                        ch.pipeline().addLast(new NettyHandler(handler));
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect(uri.getHost(), uri.getPort()).sync();
            this.channel = new NettyChannel(future.channel());
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        System.out.println("client要停机了");
                        synchronized (NettyClient.class) {
                            group.shutdownGracefully().sync();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //为了帮助invoker发送数据
    @Override
    public WrpcChannel getChannel() {
        return channel;
    }
}
