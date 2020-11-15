package com.wyber.rpc.remoting;

import com.wyber.rpc.remoting.netty.Netty4Transporter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Viber
 * @version 1.0
 * @description:
 * @date 2020/11/10 10:51
 */
public class Netty4TransporterTest {

    public static void main(String[] args) throws URISyntaxException {
        Netty4Transporter netty4Transporter = new Netty4Transporter();
        netty4Transporter.start(new URI("XXX://127.0.0.1:8080"),
                new Codec() {
                    @Override
                    public byte[] encode(Object msg) throws Exception {
                        return new byte[0];
                    }

                    @Override
                    public List<Object> decode(byte[] message) throws Exception {
                        List<Object> objects = new ArrayList<>();
                        System.out.println("打印请求的内容：" + new String(message));
                        objects.add("1:" + new String(message));
                        objects.add("2:" + new String(message));
                        objects.add("3:" + new String(message));
                        return objects;
                    }

                    @Override
                    public Codec createInstance() {
                        return this;
                    }
                },
                new Handler() {
                    @Override
                    public void onReceive(WrpcChannel wrpcChannel, Object message) throws Exception {
                        System.out.println("Handler收到的数据信息:" + message);
                    }

                    @Override
                    public void onWrite(WrpcChannel wrpcChannel, Object message) throws Exception {

                    }
                });
    }
}
