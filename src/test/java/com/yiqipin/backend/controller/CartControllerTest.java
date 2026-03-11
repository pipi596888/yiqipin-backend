package com.yiqipin.backend.controller;

import com.yiqipin.backend.dto.CartAddRequest;
import com.yiqipin.backend.entity.CartItem;
import com.yiqipin.backend.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

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
    void testGetCartList_Success() throws Exception {
        // Arrange
        List<CartItem> items = Arrays.asList(testCartItem);
        when(cartService.listByUserId(1L)).thenReturn(items);

        // Act & Assert
        mockMvc.perform(get("/api/cart/list")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].productId").value(100));
    }

    @Test
    void testGetCartList_Empty() throws Exception {
        // Arrange
        when(cartService.listByUserId(1L)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/cart/list")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testAddToCart_Success() throws Exception {
        // Arrange
        doNothing().when(cartService).addItem(anyLong(), anyLong(), anyInt());

        String jsonBody = "{\"productId\":100,\"quantity\":2}";

        // Act & Assert
        mockMvc.perform(post("/api/cart/add")
                .requestAttr("userId", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.message").value("added"));
    }

    @Test
    void testUpdateCartQuantity_Success() throws Exception {
        // Arrange
        doNothing().when(cartService).updateQuantity(anyLong(), anyLong(), anyInt());

        // Act & Assert
        mockMvc.perform(post("/api/cart/update")
                .requestAttr("userId", 1L)
                .param("productId", "100")
                .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void testRemoveFromCart_Success() throws Exception {
        // Arrange
        doNothing().when(cartService).removeItem(anyLong(), anyLong());

        // Act & Assert
        mockMvc.perform(post("/api/cart/remove")
                .requestAttr("userId", 1L)
                .param("productId", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void testClearCart_Success() throws Exception {
        // Arrange
        doNothing().when(cartService).clearCart(anyLong());

        // Act & Assert
        mockMvc.perform(post("/api/cart/clear")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}
