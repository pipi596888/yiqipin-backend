package com.yiqipin.backend.controller;

import com.yiqipin.backend.entity.Address;
import com.yiqipin.backend.entity.User;
import com.yiqipin.backend.service.AddressService;
import com.yiqipin.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AddressService addressService;

    private User testUser;
    private Address testAddress;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setGrowthPoints(100);

        testAddress = new Address();
        testAddress.setId(1L);
        testAddress.setUserId(1L);
        testAddress.setReceiver("Test User");
        testAddress.setPhone("13800138000");
        testAddress.setProvince("Guangdong");
        testAddress.setCity("Shenzhen");
    }

    @Test
    void testGetProfile_Success() throws Exception {
        // Arrange
        when(userService.getById(1L)).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(get("/api/user/profile")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    void testGetProfile_UserNotFound() throws Exception {
        // Arrange
        when(userService.getById(999L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/api/user/profile")
                .requestAttr("userId", 999L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void testGetAddresses_Success() throws Exception {
        // Arrange
        List<Address> addresses = Arrays.asList(testAddress);
        when(addressService.listByUserId(1L)).thenReturn(addresses);

        // Act & Assert
        mockMvc.perform(get("/api/user/address")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].receiver").value("Test User"));
    }

    @Test
    void testGetAddresses_Empty() throws Exception {
        // Arrange
        when(addressService.listByUserId(1L)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/user/address")
                .requestAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testAddAddress_Success() throws Exception {
        // Arrange
        when(addressService.save(any(Address.class))).thenReturn(true);

        String jsonBody = "{\"receiver\":\"New User\",\"phone\":\"13900139000\",\"province\":\"Guangdong\",\"city\":\"Shenzhen\",\"district\":\"Nanshan\",\"detail\":\"New Address\"}";

        // Act & Assert
        mockMvc.perform(post("/api/user/address")
                .requestAttr("userId", 1L)
                .contentType("application/json")
                .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void testUpdateAddress_Success() throws Exception {
        // Arrange
        when(addressService.updateById(any(Address.class))).thenReturn(true);

        String jsonBody = "{\"receiver\":\"Updated User\",\"phone\":\"13900139000\"}";

        // Act & Assert
        mockMvc.perform(put("/api/user/address/1")
                .contentType("application/json")
                .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    void testDeleteAddress_Success() throws Exception {
        // Arrange
        when(addressService.removeById(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/user/address/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}
