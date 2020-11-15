package com.wyber.rpc.order.spi;

/**
 * 订单
 */
public interface OrderService {

    /**
     * 创建订单
     *
     * @param orderContent 订单内容
     */
    void create(String orderContent);
}
