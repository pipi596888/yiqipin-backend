package com.yiqipin.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderCreateRequest(
        @NotNull Long addressId,
        String remark,
        @NotEmpty List<OrderItemRequest> items,
        @NotNull Double total_price) {
    public record OrderItemRequest(Long productId, String name, Double price, Integer quantity) {}
}
