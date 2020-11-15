package com.wyber.rpc.remoting.netty;

import com.wyber.rpc.remoting.*;

import java.net.URI;

/**
 * @author Viber
 * @version 1.0
 * @description:
 * @date 2020/11/10 10:37
 */
public class Netty4Transporter implements Transporter {
    public Server start(URI uri, Codec codec, Handler handler) {
        NettyServer server = new NettyServer();
        server.start(uri, codec, handler);
        return server;
    }

    @Override
    public Client connect(URI uri, Codec codec, Handler handler) {
        NettyClient client = new NettyClient();
        client.connect(uri, codec, handler);
        return client;
    }
}
