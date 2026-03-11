package com.yiqipin.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiqipin.backend.entity.CartItem;
import com.yiqipin.backend.mapper.CartItemMapper;
import com.yiqipin.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements CartService {

    @Override
    @Cacheable(value = "cart", key = "'user_' + #userId")
    public List<CartItem> listByUserId(Long userId) {
        return this.list(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .orderByDesc(CartItem::getCreatedAt));
    }

    @Override
    @CacheEvict(value = "cart", key = "'user_' + #userId")
    public void addItem(Long userId, Long productId, int quantity) {
        // Check if item already exists in cart
        CartItem existingItem = this.getOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, productId));

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setUpdatedAt(LocalDateTime.now());
            this.updateById(existingItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);
            cartItem.setCreatedAt(LocalDateTime.now());
            cartItem.setUpdatedAt(LocalDateTime.now());
            this.save(cartItem);
        }
    }

    @Override
    @CacheEvict(value = "cart", key = "'user_' + #userId")
    public void updateQuantity(Long userId, Long productId, int quantity) {
        CartItem item = this.getOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, productId));

        if (item != null) {
            if (quantity <= 0) {
                this.removeById(item.getId());
            } else {
                item.setQuantity(quantity);
                item.setUpdatedAt(LocalDateTime.now());
                this.updateById(item);
            }
        }
    }

    @Override
    @CacheEvict(value = "cart", key = "'user_' + #userId")
    public void removeItem(Long userId, Long productId) {
        this.remove(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, productId));
    }

    @Override
    @CacheEvict(value = "cart", key = "'user_' + #userId")
    public void clearCart(Long userId) {
        this.remove(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId));
    }
}
