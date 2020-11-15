package com.wyber.rpc.rpc.protocol.wrpc.handler;

import com.wyber.rpc.common.serialize.Serialization;
import com.wyber.rpc.remoting.Handler;
import com.wyber.rpc.remoting.WrpcChannel;
import com.wyber.rpc.rpc.Invoker;
import com.wyber.rpc.rpc.Response;
import com.wyber.rpc.rpc.RpcInvocation;

/**
 * @author Viber
 * @version 1.0
 * @description:
 * @date 2020/11/10 13:46
 */
public class WrpcServerHandler implements Handler {

    Invoker invoker;
    Serialization serialization;

    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    public void setSerialization(Serialization serialization) {
        this.serialization = serialization;
    }

    @Override
    public void onReceive(WrpcChannel wrpcChannel, Object message) throws Exception {
        //RpcInvocation
        RpcInvocation rpcInvocation = (RpcInvocation) message;
        System.out.println("收到invocation 准备发送数据:" + rpcInvocation);
        //调用类和方法
        Response response = new Response();
        response.setRequestId(rpcInvocation.getId());
        try {
            Object result = invoker.invoke(rpcInvocation);
            System.out.println("服务端调用结果:" + result);
            response.setStatus(200);
            response.setContent(result);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(99);
            response.setContent(e.getMessage());
        }
        //发送,序列化 -- responseBody
        wrpcChannel.send(serialization.serialize(response));
    }

    @Override
    public void onWrite(WrpcChannel wrpcChannel, Object message) throws Exception {
        //写response
    }
}
