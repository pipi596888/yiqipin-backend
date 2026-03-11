package com.yiqipin.backend.controller;

import com.yiqipin.backend.common.ApiResponse;
import com.yiqipin.backend.dto.CartAddRequest;
import com.yiqipin.backend.entity.CartItem;
import com.yiqipin.backend.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/cart/list")
    public ApiResponse<List<CartItem>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<CartItem> list = cartService.listByUserId(userId);
        return ApiResponse.success(list);
    }

    @PostMapping("/cart/add")
    public ApiResponse<Map<String, Object>> add(@Valid @RequestBody CartAddRequest cartRequest, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.addItem(userId, cartRequest.getProductId(), cartRequest.getQuantity());
        return ApiResponse.success(Map.of(
                "product_id", cartRequest.getProductId(),
                "quantity", cartRequest.getQuantity(),
                "message", "added"
        ));
    }

    @PostMapping("/cart/update")
    public ApiResponse<Void> update(@RequestParam Long productId, @RequestParam Integer quantity, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.updateQuantity(userId, productId, quantity);
        return ApiResponse.success(null);
    }

    @PostMapping("/cart/remove")
    public ApiResponse<Void> remove(@RequestParam Long productId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.removeItem(userId, productId);
        return ApiResponse.success(null);
    }

    @PostMapping("/cart/clear")
    public ApiResponse<Void> clear(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.clearCart(userId);
        return ApiResponse.success(null);
    }
}
