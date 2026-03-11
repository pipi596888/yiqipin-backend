package com.yiqipin.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yiqipin.backend.dto.OrderItemRequest;
import com.yiqipin.backend.dto.OrderPayRequest;
import com.yiqipin.backend.entity.Order;
import com.yiqipin.backend.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private Order testOrder;
    private OrderItemRequest testOrderItem;
    private List<OrderItemRequest> testItems;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNo("YP20240301120000ABC12345");
        testOrder.setUserId(1L);
        testOrder.setStatus("pending");
        testOrder.setTotalPrice(new BigDecimal("99.99"));
        testOrder.setPayPrice(new BigDecimal("99.99"));

        testOrderItem = new OrderItemRequest(100L, "Test Product", 49.99, 2);
        testItems = List.of(testOrderItem);
    }

    @Test
    void testGetOrderList_Success() throws Exception {
        // Arrange
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.listByUserId(1L)).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/api/order/list")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].orderNo").value("YP20240301120000ABC12345"));
    }

    @Test
    void testGetOrderList_Empty() throws Exception {
        // Arrange
        when(orderService.listByUserId(1L)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/order/list")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testGetOrderDetail_Success() throws Exception {
        // Arrange
        when(orderService.getOne(any())).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(get("/api/order/YP20240301120000ABC12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.orderNo").value("YP20240301120000ABC12345"));
    }

    @Test
    void testGetOrderDetail_NotFound() throws Exception {
        // Arrange
        when(orderService.getOne(any())).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/order/NONEXISTENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("Order not found"));
    }

    @Test
    void testCreateOrder_Success() throws Exception {
        // Arrange - Use record syntax matching actual DTO
        String jsonBody = "{\"addressId\":1,\"remark\":\"Test remark\",\"items\":[{\"productId\":100,\"name\":\"Test Product\",\"price\":49.99,\"quantity\":2}],\"total_price\":99.98}";

        when(orderService.createOrder(anyLong(), anyLong(), anyString())).thenReturn(testOrder);

        // Act & Assert
        mockMvc.perform(post("/api/order/create")
                .requestAttr("userId", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.order_id").value("YP20240301120000ABC12345"));
    }

    @Test
    void testCreateOrder_EmptyCart() throws Exception {
        // Arrange
        String jsonBody = "{\"addressId\":1,\"remark\":\"\",\"items\":[{\"productId\":100,\"name\":\"Test Product\",\"price\":49.99,\"quantity\":2}],\"total_price\":99.98}";
        when(orderService.createOrder(anyLong(), anyLong(), anyString()))
                .thenThrow(new RuntimeException("Cart is empty"));

        // Act & Assert
        mockMvc.perform(post("/api/order/create")
                .requestAttr("userId", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("Cart is empty"));
    }

    @Test
    void testPayOrder_Success() throws Exception {
        // Arrange
        OrderPayRequest request = new OrderPayRequest("YP20240301120000ABC12345");
        when(orderService.payOrder("YP20240301120000ABC12345")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/order/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.status").value("paid"));
    }

    @Test
    void testPayOrder_Failed() throws Exception {
        // Arrange
        OrderPayRequest request = new OrderPayRequest("YP20240301120000ABC12345");
        when(orderService.payOrder("YP20240301120000ABC12345")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/order/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("Payment failed"));
    }

    @Test
    void testCancelOrder_Success() throws Exception {
        // Arrange
        when(orderService.cancelOrder("YP20240301120000ABC12345")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/order/cancel")
                .param("orderNo", "YP20240301120000ABC12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void testCancelOrder_Failed() throws Exception {
        // Arrange
        when(orderService.cancelOrder("YP20240301120000ABC12345")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/order/cancel")
                .param("orderNo", "YP20240301120000ABC12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("Cancel failed"));
    }
}
