package com.yiqipin.backend.model;

public record OrderRecord(String orderId, Long userId, double totalPrice, String status, String createTime) {}
