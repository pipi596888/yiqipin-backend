package com.yiqipin.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yiqipin.backend.entity.Order;

import java.util.List;

public interface OrderService extends IService<Order> {

    List<Order> listByUserId(Long userId);

    Order createOrder(Long userId, Long addressId, String remark);

    boolean payOrder(String orderNo);

    boolean shipOrder(String orderNo);

    boolean receiveOrder(String orderNo);

    boolean cancelOrder(String orderNo);
}
