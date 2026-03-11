package com.yiqipin.backend.controller;

import com.yiqipin.backend.common.ApiResponse;
import com.yiqipin.backend.entity.Product;
import com.yiqipin.backend.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ApiResponse<Map<String, List<Product>>> products() {
        List<Product> list = productService.listOnSale();
        Map<String, List<Product>> data = new HashMap<>();
        data.put("list", list);
        return ApiResponse.success(data);
    }

    @GetMapping("/product/{id}")
    public ApiResponse<?> productDetail(@PathVariable Long id) {
        Product product = productService.getProductDetail(id);
        if (product != null) {
            return ApiResponse.success(product);
        }
        return ApiResponse.fail("Product not found");
    }
}
