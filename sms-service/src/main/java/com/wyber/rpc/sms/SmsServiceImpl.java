package com.wyber.rpc.sms;


import com.wyber.rpc.sms.spi.SmsService;
import com.wyber.rpc.spring.annotation.WRpcService;

import java.util.UUID;

// 面向java接口 远程调用
@WRpcService // 告诉rpc框架 此服务需要开放
public class SmsServiceImpl implements SmsService {
    public Object send(String phone, String content) {
        System.out.println("发送短信：" + phone + ":" + content);
        return "短信发送成功" + UUID.randomUUID().toString();
    }
}
