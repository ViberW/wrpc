package com.wyber.rpc.remoting;

import java.net.URI;

/**
 * 服务端
 */
public interface Server {
    void start(URI uri, Codec codec, Handler handler);
}
