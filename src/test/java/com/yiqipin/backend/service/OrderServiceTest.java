package com.yiqipin.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yiqipin.backend.entity.*;
import com.yiqipin.backend.mapper.OrderItemMapper;
import com.yiqipin.backend.mapper.OrderMapper;
import com.yiqipin.backend.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private CartService cartService;

    @Mock
    private ProductService productService;

    @Mock
    private AddressService addressService;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private GrowthRecordMapper growthRecordMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private CartItem testCartItem;
    private Product testProduct;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNo("YP20240301120000ABC12345");
        testOrder.setUserId(1L);
        testOrder.setStatus("pending");
        testOrder.setTotalPrice(new BigDecimal("99.99"));
        testOrder.setPayPrice(new BigDecimal("99.99"));

        testCartItem = new CartItem();
        testCartItem.setUserId(1L);
        testCartItem.setProductId(100L);
        testCartItem.setQuantity(2);

        testProduct = new Product();
        testProduct.setId(100L);
        testProduct.setName("Test Product");
        testProduct.setPrice(new BigDecimal("49.99"));
        testProduct.setStock(100);

        testAddress = new Address();
        testAddress.setId(1L);
        testAddress.setReceiver("Test User");
        testAddress.setPhone("13800138000");
        testAddress.setProvince("Guangdong");
        testAddress.setCity("Shenzhen");
        testAddress.setDistrict("Nanshan");
        testAddress.setDetail("Test Address");
    }

    @Test
    void testListByUserId_Success() {
        // Arrange
        List<Order> orders = Arrays.asList(testOrder);
        when(orderMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(orders);

        // Act
        List<Order> result = orderService.listByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("YP20240301120000ABC12345", result.get(0).getOrderNo());
    }

    @Test
    void testListByUserId_Empty() {
        // Arrange
        when(orderMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        // Act
        List<Order> result = orderService.listByUserId(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateOrder_Success() {
        // Arrange
        when(cartService.listByUserId(1L)).thenReturn(Arrays.asList(testCartItem));
        when(productService.getProductDetail(100L)).thenReturn(testProduct);
        when(productService.reduceStock(anyLong(), anyInt())).thenReturn(true);
        when(addressService.getById(1L)).thenReturn(testAddress);
        when(orderMapper.insert(any(Order.class))).thenReturn(1);
        when(orderItemMapper.insert(any(OrderItem.class))).thenReturn(1);
        when(orderMapper.updateById(any(Order.class))).thenReturn(true);

        // Act
        Order result = orderService.createOrder(1L, 1L, "Test remark");

        // Assert
        assertNotNull(result);
        assertNotNull(result.getOrderNo());
        assertTrue(result.getOrderNo().startsWith("YP"));
        verify(cartService, times(1)).clearCart(1L);
    }

    @Test
    void testCreateOrder_EmptyCart() {
        // Arrange
        when(cartService.listByUserId(1L)).thenReturn(Collections.emptyList());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> orderService.createOrder(1L, 1L, ""));

        assertEquals("Cart is empty", exception.getMessage());
    }

    @Test
    void testCreateOrder_OutOfStock() {
        // Arrange
        testProduct.setStock(0);
        when(cartService.listByUserId(1L)).thenReturn(Arrays.asList(testCartItem));
        when(productService.getProductDetail(100L)).thenReturn(testProduct);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> orderService.createOrder(1L, 1L, ""));

        assertTrue(exception.getMessage().contains("Product out of stock"));
    }

    @Test
    void testPayOrder_Success() {
        // Arrange
        when(orderMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testOrder);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        // Act
        boolean result = orderService.payOrder("YP20240301120000ABC12345");

        // Assert
        assertTrue(result);
        assertEquals("paid", testOrder.getStatus());
        verify(userService, times(1)).updateGrowthPoints(eq(1L), anyInt());
        verify(growthRecordMapper, times(1)).insert(any(GrowthRecord.class));
    }

    @Test
    void testPayOrder_OrderNotFound() {
        // Arrange
        when(orderMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // Act
        boolean result = orderService.payOrder("NONEXISTENT");

        // Assert
        assertFalse(result);
    }

    @Test
    void testPayOrder_WrongStatus() {
        // Arrange
        testOrder.setStatus("completed"); // Already completed
        when(orderMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testOrder);

        // Act
        boolean result = orderService.payOrder("YP20240301120000ABC12345");

        // Assert
        assertFalse(result);
    }

    @Test
    void testShipOrder_Success() {
        // Arrange
        testOrder.setStatus("paid");
        when(orderMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testOrder);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        // Act
        boolean result = orderService.shipOrder("YP20240301120000ABC12345");

        // Assert
        assertTrue(result);
        assertEquals("shipped", testOrder.getStatus());
    }

    @Test
    void testReceiveOrder_Success() {
        // Arrange
        testOrder.setStatus("shipped");
        when(orderMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testOrder);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        // Act
        boolean result = orderService.receiveOrder("YP20240301120000ABC12345");

        // Assert
        assertTrue(result);
        assertEquals("completed", testOrder.getStatus());
    }

    @Test
    void testCancelOrder_Success() {
        // Arrange
        testOrder.setStatus("pending");
        when(orderMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testOrder);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        // Act
        boolean result = orderService.cancelOrder("YP20240301120000ABC12345");

        // Assert
        assertTrue(result);
        assertEquals("cancelled", testOrder.getStatus());
    }

    @Test
    void testCancelOrder_WrongStatus() {
        // Arrange
        testOrder.setStatus("shipped"); // Already shipped, cannot cancel
        when(orderMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testOrder);

        // Act
        boolean result = orderService.cancelOrder("YP20240301120000ABC12345");

        // Assert
        assertFalse(result);
    }
}
