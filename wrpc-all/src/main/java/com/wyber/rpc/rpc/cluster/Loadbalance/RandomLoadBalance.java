package com.wyber.rpc.rpc.cluster.Loadbalance;

import com.wyber.rpc.rpc.Invoker;
import com.wyber.rpc.rpc.cluster.LoadBalance;

import java.net.URI;
import java.util.Map;
import java.util.Random;

/**
 * @author Viber
 * @version 1.0
 * @description:
 * @date 2020/11/12 10:52
 */
public class RandomLoadBalance implements LoadBalance {
    @Override
    public Invoker select(Map<URI, Invoker> invokerMap) {
        int index = new Random().nextInt(invokerMap.values().size());
        return invokerMap.values().toArray(new Invoker[]{})[index];
    }
}
