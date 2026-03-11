package com.yiqipin.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yiqipin.backend.entity.CartItem;
import com.yiqipin.backend.mapper.CartItemMapper;
import com.yiqipin.backend.service.impl.CartServiceImpl;
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
class CartServiceTest {

    @Mock
    private CartItemMapper cartItemMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setUserId(1L);
        testCartItem.setProductId(100L);
        testCartItem.setQuantity(2);
    }

    @Test
    void testListByUserId_Success() {
        // Arrange
        List<CartItem> items = Arrays.asList(testCartItem);
        when(cartItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(items);

        // Act
        List<CartItem> result = cartService.listByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getQuantity());
    }

    @Test
    void testListByUserId_Empty() {
        // Arrange
        when(cartItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList());

        // Act
        List<CartItem> result = cartService.listByUserId(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testAddItem_NewItem() {
        // Arrange
        when(cartItemMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(cartItemMapper.insert(any(CartItem.class))).thenReturn(1);

        // Act
        cartService.addItem(1L, 100L, 2);

        // Assert
        verify(cartItemMapper, times(1)).insert(any(CartItem.class));
    }

    @Test
    void testAddItem_UpdateExistingItem() {
        // Arrange
        when(cartItemMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testCartItem);
        when(cartItemMapper.updateById(any(CartItem.class))).thenReturn(1);

        // Act
        cartService.addItem(1L, 100L, 3);

        // Assert
        verify(cartItemMapper, times(1)).updateById(any(CartItem.class));
        assertEquals(5, testCartItem.getQuantity()); // 2 + 3
    }

    @Test
    void testUpdateQuantity_Success() {
        // Arrange
        when(cartItemMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testCartItem);
        when(cartItemMapper.updateById(any(CartItem.class))).thenReturn(1);

        // Act
        cartService.updateQuantity(1L, 100L, 5);

        // Assert
        verify(cartItemMapper, times(1)).updateById(any(CartItem.class));
    }

    @Test
    void testUpdateQuantity_RemoveWhenZero() {
        // Arrange
        when(cartItemMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testCartItem);

        // Act
        cartService.updateQuantity(1L, 100L, 0);

        // Assert
        verify(cartItemMapper, times(1)).removeById(testCartItem.getId());
    }

    @Test
    void testUpdateQuantity_ItemNotFound() {
        // Arrange
        when(cartItemMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // Act
        cartService.updateQuantity(1L, 100L, 5);

        // Assert
        verify(cartItemMapper, never()).updateById(any(CartItem.class));
    }

    @Test
    void testRemoveItem_Success() {
        // Arrange
        when(cartItemMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);

        // Act
        cartService.removeItem(1L, 100L);

        // Assert
        verify(cartItemMapper, times(1)).delete(any(LambdaQueryWrapper.class));
    }

    @Test
    void testClearCart_Success() {
        // Arrange
        when(cartItemMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(5);

        // Act
        cartService.clearCart(1L);

        // Assert
        verify(cartItemMapper, times(1)).delete(any(LambdaQueryWrapper.class));
    }
}
