package com.yiqipin.backend.model;

import java.util.List;

public record Product(
        Long productId,
        String name,
        double price,
        int stock,
        int sales,
        double rating,
        String image,
        List<String> images,
        String desc
) {}
