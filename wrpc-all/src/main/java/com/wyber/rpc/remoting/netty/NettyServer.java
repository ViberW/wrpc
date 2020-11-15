package com.wyber.rpc.remoting.netty;

import com.wyber.rpc.remoting.Codec;
import com.wyber.rpc.remoting.Handler;
import com.wyber.rpc.remoting.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.net.URI;

/**
 * @author Viber
 * @version 1.0
 * @description: 使用netty开启
 * @date 2020/11/10 10:38
 */
public class NettyServer implements Server {
    NioEventLoopGroup boss = new NioEventLoopGroup();
    NioEventLoopGroup worker = new NioEventLoopGroup();

    public void start(URI uri, Codec codec, Handler handler) {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .localAddress(new InetSocketAddress(uri.getHost(), uri.getPort()))
                    .channel(NioServerSocketChannel.class) //使用NioServerSocketChannel接收链接
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //ChannelInitializer最终会自己取消注册自己
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyCodec(codec.createInstance()));
                            ch.pipeline().addLast(new NettyHandler(handler));
                        }
                    });
            ChannelFuture future = bootstrap.bind().sync();
            System.out.println("NettyServer启动成功,准备接收数据");
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        System.out.println("server要停机了");
                        synchronized (NettyClient.class) {
                            boss.shutdownGracefully().sync();
                            worker.shutdownGracefully().sync();
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
}
