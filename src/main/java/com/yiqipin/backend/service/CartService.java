package com.yiqipin.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yiqipin.backend.entity.CartItem;

import java.util.List;

public interface CartService extends IService<CartItem> {

    List<CartItem> listByUserId(Long userId);

    void addItem(Long userId, Long productId, int quantity);

    void updateQuantity(Long userId, Long productId, int quantity);

    void removeItem(Long userId, Long productId);

    void clearCart(Long userId);
}
