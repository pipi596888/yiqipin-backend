package com.yiqipin.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiqipin.backend.entity.Product;
import com.yiqipin.backend.mapper.ProductMapper;
import com.yiqipin.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    @Cacheable(value = "products", key = "'listOnSale'")
    public List<Product> listOnSale() {
        return this.list(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getSales));
    }

    @Override
    @Cacheable(value = "product", key = "#id")
    public Product getProductDetail(Long id) {
        return this.getById(id);
    }

    @Override
    public boolean reduceStock(Long productId, int quantity) {
        Product product = this.getById(productId);
        if (product != null && product.getStock() >= quantity) {
            product.setStock(product.getStock() - quantity);
            product.setSales(product.getSales() + quantity);
            return this.updateById(product);
        }
        return false;
    }
}
