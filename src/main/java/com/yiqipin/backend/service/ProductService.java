package com.yiqipin.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yiqipin.backend.entity.Product;

import java.util.List;

public interface ProductService extends IService<Product> {

    List<Product> listOnSale();

    Product getProductDetail(Long id);

    boolean reduceStock(Long productId, int quantity);
}
