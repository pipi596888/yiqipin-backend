package com.yiqipin.backend.model;

public record RecommendationItem(Long productId, String title, double rankScore, double salePrice, String cover) {}
