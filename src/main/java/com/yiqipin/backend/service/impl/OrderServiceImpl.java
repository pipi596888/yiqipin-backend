package com.yiqipin.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiqipin.backend.entity.*;
import com.yiqipin.backend.mapper.OrderItemMapper;
import com.yiqipin.backend.mapper.OrderMapper;
import com.yiqipin.backend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final CartService cartService;
    private final ProductService productService;
    private final AddressService addressService;
    private final OrderItemMapper orderItemMapper;
    private final GrowthRecordMapper growthRecordMapper;
    private final UserService userService;

    private static final DateTimeFormatter ORDER_NO_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    public List<Order> listByUserId(Long userId) {
        return this.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreatedAt));
    }

    @Override
    @Transactional
    public Order createOrder(Long userId, Long addressId, String remark) {
        // Get cart items
        List<CartItem> cartItems = cartService.listByUserId(userId);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Calculate total price and create order items
        BigDecimal totalPrice = BigDecimal.ZERO;
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setStatus("pending");
        order.setRemark(remark);

        // Get address info
        Address address = addressService.getById(addressId);
        if (address != null) {
            order.setReceiver(address.getReceiver());
            order.setPhone(address.getPhone());
            order.setAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetail());
        }

        // Create order
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        this.save(order);

        // Create order items and reduce stock
        for (CartItem cartItem : cartItems) {
            Product product = productService.getProductDetail(cartItem.getProductId());
            if (product == null || product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Product out of stock: " + (product != null ? product.getName() : ""));
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getImage());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(itemTotal);
            orderItem.setCreatedAt(LocalDateTime.now());
            orderItemMapper.insert(orderItem);

            // Reduce stock
            productService.reduceStock(product.getId(), cartItem.getQuantity());
        }

        order.setTotalPrice(totalPrice);
        order.setPayPrice(totalPrice);
        this.updateById(order);

        // Clear cart after order created
        cartService.clearCart(userId);

        return order;
    }

    @Override
    @Transactional
    public boolean payOrder(String orderNo) {
        Order order = this.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo));

        if (order != null && "pending".equals(order.getStatus())) {
            order.setStatus("paid");
            order.setPayTime(LocalDateTime.now());
            order.setPayMethod("wechat");
            order.setUpdatedAt(LocalDateTime.now());
            this.updateById(order);

            // Add growth points (1 point per 1 yuan)
            int points = order.getPayPrice().intValue();
            userService.updateGrowthPoints(order.getUserId(), points);

            // Record growth
            GrowthRecord record = new GrowthRecord();
            record.setUserId(order.getUserId());
            record.setType("order");
            record.setPoints(points);
            record.setDescription("Order payment: " + orderNo);
            record.setCreatedAt(LocalDateTime.now());
            growthRecordMapper.insert(record);

            return true;
        }
        return false;
    }

    @Override
    public boolean shipOrder(String orderNo) {
        Order order = this.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo));

        if (order != null && "paid".equals(order.getStatus())) {
            order.setStatus("shipped");
            order.setShipTime(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            return this.updateById(order);
        }
        return false;
    }

    @Override
    public boolean receiveOrder(String orderNo) {
        Order order = this.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo));

        if (order != null && "shipped".equals(order.getStatus())) {
            order.setStatus("completed");
            order.setReceiveTime(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            return this.updateById(order);
        }
        return false;
    }

    @Override
    public boolean cancelOrder(String orderNo) {
        Order order = this.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo));

        if (order != null && "pending".equals(order.getStatus())) {
            order.setStatus("cancelled");
            order.setUpdatedAt(LocalDateTime.now());
            return this.updateById(order);
        }
        return false;
    }

    private String generateOrderNo() {
        return "YP" + LocalDateTime.now().format(ORDER_NO_FMT) + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
