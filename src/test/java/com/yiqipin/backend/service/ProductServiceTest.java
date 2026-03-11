package com.yiqipin.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yiqipin.backend.entity.Product;
import com.yiqipin.backend.mapper.ProductMapper;
import com.yiqipin.backend.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(99.99);
        testProduct.setStock(100);
        testProduct.setSales(50);
        testProduct.setStatus(1);
    }

    @Test
    void testListOnSale_Success() {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(products);

        // Act
        List<Product> result = productService.listOnSale();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
        verify(productMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    void testListOnSale_Empty() {
        // Arrange
        when(productMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList());

        // Act
        List<Product> result = productService.listOnSale();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetProductDetail_Success() {
        // Arrange
        when(productMapper.selectById(1L)).thenReturn(testProduct);

        // Act
        Product result = productService.getProductDetail(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(99.99, result.getPrice());
    }

    @Test
    void testGetProductDetail_NotFound() {
        // Arrange
        when(productMapper.selectById(999L)).thenReturn(null);

        // Act
        Product result = productService.getProductDetail(999L);

        // Assert
        assertNull(result);
    }

    @Test
    void testReduceStock_Success() {
        // Arrange
        when(productMapper.selectById(1L)).thenReturn(testProduct);
        when(productMapper.updateById(any(Product.class))).thenReturn(1);

        // Act
        boolean result = productService.reduceStock(1L, 10);

        // Assert
        assertTrue(result);
        assertEquals(90, testProduct.getStock()); // 100 - 10
        assertEquals(60, testProduct.getSales()); // 50 + 10
        verify(productMapper, times(1)).updateById(any(Product.class));
    }

    @Test
    void testReduceStock_InsufficientStock() {
        // Arrange
        testProduct.setStock(5);
        when(productMapper.selectById(1L)).thenReturn(testProduct);

        // Act
        boolean result = productService.reduceStock(1L, 10);

        // Assert
        assertFalse(result);
        verify(productMapper, never()).updateById(any(Product.class));
    }

    @Test
    void testReduceStock_ProductNotFound() {
        // Arrange
        when(productMapper.selectById(999L)).thenReturn(null);

        // Act
        boolean result = productService.reduceStock(999L, 10);

        // Assert
        assertFalse(result);
    }
}
