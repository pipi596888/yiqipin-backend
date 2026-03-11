package com.yiqipin.backend.service;

import com.yiqipin.backend.model.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MockMallService {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final List<Product> products = List.of(
            new Product(1001L, "Wireless Earbuds", 199, 120, 985, 4.8, "/static/logo.png", List.of("/static/logo.png"), "Noise-canceling earbuds."),
            new Product(1002L, "Mechanical Keyboard", 329, 80, 463, 4.7, "/static/logo.png", List.of("/static/logo.png"), "Compact 87-key keyboard."),
            new Product(1003L, "Fitness Band", 149, 240, 1510, 4.6, "/static/logo.png", List.of("/static/logo.png"), "Daily health tracking band.")
    );

    private final List<Campaign> campaigns = List.of(
            new Campaign(1L, "New User Coupon", "coupon", "active", "10%"),
            new Campaign(2L, "Flash Sale", "flash_sale", "scheduled", "$20 off"),
            new Campaign(3L, "Group Buy", "group_buy", "active", "15%")
    );

    private final List<RecommendationItem> hotList = List.of(
            new RecommendationItem(1001L, "Wireless Earbuds", 9.6, 199, "/static/logo.png"),
            new RecommendationItem(1002L, "Mechanical Keyboard", 9.1, 329, "/static/logo.png")
    );

    private final List<RecommendationItem> guessList = List.of(
            new RecommendationItem(1003L, "Fitness Band", 8.9, 149, "/static/logo.png"),
            new RecommendationItem(1002L, "Mechanical Keyboard", 8.7, 329, "/static/logo.png")
    );

    private final Map<String, OrderRecord> orderStore = new ConcurrentHashMap<>();
    private final AtomicLong orderSeq = new AtomicLong(10000);

    public Map<String, Object> login(String username) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1);
        user.put("username", username);
        user.put("phone", "13800000000");
        user.put("avatar", "/static/logo.png");

        Map<String, Object> data = new HashMap<>();
        data.put("token", "token-" + System.currentTimeMillis());
        data.put("user", user);
        return data;
    }

    public List<Product> productList() {
        return products;
    }

    public Optional<Product> productDetail(Long id) {
        return products.stream().filter(p -> p.productId().equals(id)).findFirst();
    }

    public Map<String, Object> createOrder(Long userId, double totalPrice) {
        String orderId = "MOCK" + orderSeq.incrementAndGet();
        OrderRecord order = new OrderRecord(orderId, userId, totalPrice, "pending", LocalDateTime.now().format(TIME_FMT));
        orderStore.put(orderId, order);

        Map<String, Object> data = new HashMap<>();
        data.put("order_id", orderId);
        data.put("total_price", totalPrice);
        data.put("status", "pending");
        return data;
    }

    public Map<String, Object> payOrder(String orderId) {
        OrderRecord prev = orderStore.get(orderId);
        if (prev != null) {
            orderStore.put(orderId, new OrderRecord(prev.orderId(), prev.userId(), prev.totalPrice(), "paid", prev.createTime()));
        }
        Map<String, Object> data = new HashMap<>();
        data.put("order_id", orderId);
        data.put("status", "paid");
        return data;
    }

    public List<OrderRecord> userOrders(Long userId) {
        if (orderStore.isEmpty()) {
            return List.of(
                    new OrderRecord("MOCK10001", userId, 199, "paid", "2026-03-11 09:00:00"),
                    new OrderRecord("MOCK10002", userId, 329, "pending", "2026-03-11 10:00:00")
            );
        }
        return orderStore.values().stream().filter(o -> Objects.equals(o.userId(), userId)).toList();
    }

    public UserProfile userProfile() {
        return new UserProfile(1L, "demo_user", "13800000000", "demo@example.com", "/static/logo.png");
    }

    public List<Address> userAddress() {
        return List.of(new Address(1L, "Demo User", "13800000000", "No.1 Demo Road, Shanghai"));
    }

    public Map<String, Object> campaigns() {
        Map<String, Object> data = new HashMap<>();
        data.put("list", campaigns);
        return data;
    }

    public Map<String, Object> recommendations() {
        Map<String, Object> data = new HashMap<>();
        data.put("hot_list", hotList);
        data.put("guess_list", guessList);
        return data;
    }

    public Map<String, Object> analytics() {
        Map<String, Object> data = new HashMap<>();
        data.put("salesToday", 12580.4);
        data.put("salesYesterday", 11820.2);
        data.put("orderCountToday", 218);
        data.put("orderCountYesterday", 204);
        data.put("userGrowthToday", 36);
        data.put("userGrowthYesterday", 29);
        data.put("topProducts", List.of(
                new TopProduct("Wireless Earbuds", 85),
                new TopProduct("Fitness Band", 72),
                new TopProduct("Mechanical Keyboard", 61)
        ));
        return data;
    }
}
