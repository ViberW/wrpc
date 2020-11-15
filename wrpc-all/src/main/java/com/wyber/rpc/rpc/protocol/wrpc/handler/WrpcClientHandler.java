package com.wyber.rpc.rpc.protocol.wrpc.handler;

import com.wyber.rpc.remoting.Handler;
import com.wyber.rpc.remoting.WrpcChannel;
import com.wyber.rpc.rpc.Response;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Viber
 * @version 1.0
 * @description:
 * @date 2020/11/12 10:03
 */
public class WrpcClientHandler implements Handler {

    final static Map<Long, CompletableFuture<Response>> futures = new ConcurrentHashMap<>();

    public static CompletableFuture<Response> waitResult(long requestId) {
        CompletableFuture<Response> future = new CompletableFuture<>();
        futures.put(requestId, future);
        return future;
    }

    @Override
    public void onReceive(WrpcChannel wrpcChannel, Object message) throws Exception {
        //Response
        Response response = (Response) message;
        //保存到一个地方,用于通信
        futures.get(response.getRequestId()).complete(response);
        futures.remove(response.getRequestId());
    }

    @Override
    public void onWrite(WrpcChannel wrpcChannel, Object message) throws Exception {

    }
}
