package com.yiqipin.backend.controller;

import com.yiqipin.backend.common.ApiResponse;
import com.yiqipin.backend.dto.OrderCreateRequest;
import com.yiqipin.backend.dto.OrderPayRequest;
import com.yiqipin.backend.entity.Order;
import com.yiqipin.backend.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/order/list")
    public ApiResponse<List<Order>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Order> orders = orderService.listByUserId(userId);
        return ApiResponse.success(orders);
    }

    @GetMapping("/order/{orderNo}")
    public ApiResponse<Order> detail(@PathVariable String orderNo) {
        Order order = orderService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Order>()
                        .eq(Order::getOrderNo, orderNo)
        );
        if (order != null) {
            return ApiResponse.success(order);
        }
        return ApiResponse.fail("Order not found");
    }

    @PostMapping("/order/create")
    public ApiResponse<Map<String, Object>> createOrder(@Valid @RequestBody OrderCreateRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        try {
            Order order = orderService.createOrder(userId, request.getAddressId(), request.getRemark());
            Map<String, Object> result = new HashMap<>();
            result.put("order_id", order.getOrderNo());
            result.put("total_price", order.getTotalPrice());
            result.put("status", order.getStatus());
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/order/pay")
    public ApiResponse<Map<String, Object>> payOrder(@Valid @RequestBody OrderPayRequest request) {
        try {
            boolean success = orderService.payOrder(request.getOrderId());
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("order_id", request.getOrderId());
                result.put("status", "paid");
                return ApiResponse.success(result);
            }
            return ApiResponse.fail("Payment failed");
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/order/cancel")
    public ApiResponse<Void> cancel(@RequestParam String orderNo) {
        boolean success = orderService.cancelOrder(orderNo);
        if (success) {
            return ApiResponse.success(null);
        }
        return ApiResponse.fail("Cancel failed");
    }
}
