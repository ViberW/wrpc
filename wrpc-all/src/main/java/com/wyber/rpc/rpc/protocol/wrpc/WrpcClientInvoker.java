package com.wyber.rpc.rpc.protocol.wrpc;

import com.wyber.rpc.common.serialize.Serialization;
import com.wyber.rpc.remoting.Client;
import com.wyber.rpc.rpc.Invoker;
import com.wyber.rpc.rpc.Response;
import com.wyber.rpc.rpc.RpcInvocation;
import com.wyber.rpc.rpc.protocol.wrpc.handler.WrpcClientHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Viber
 * @version 1.0
 * @description:
 * @date 2020/11/12 9:44
 */
public class WrpcClientInvoker implements Invoker {

    Client client;
    Serialization serialization;

    public WrpcClientInvoker(Client client, Serialization serialization) {
        this.client = client;
        this.serialization = serialization;
    }

    @Override
    public Class getInterface() {
        return null;
    }

    @Override
    public Object invoke(RpcInvocation rpcInvocation) throws Exception {
        byte[] requestBody = serialization.serialize(rpcInvocation);
        client.getChannel().send(requestBody);
        //异步通信
        CompletableFuture<Response> future = WrpcClientHandler.waitResult(rpcInvocation.getId());
        Object result = future.get(60, TimeUnit.SECONDS);
        Response response = (Response) result;
        if (response.getStatus() == 200) {
            return response.getContent();
        } else {
            throw new Exception("server error:" + response.getStatus()
                    + " with requestId:" + response.getRequestId());
        }
    }
}
