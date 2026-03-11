package com.yiqipin.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yiqipin.backend.entity.GrowthRecord;
import com.yiqipin.backend.entity.User;
import com.yiqipin.backend.mapper.GrowthRecordMapper;
import com.yiqipin.backend.mapper.UserMapper;
import com.yiqipin.backend.security.JwtUtil;
import com.yiqipin.backend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private GrowthRecordMapper growthRecordMapper;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setPhone("13800138000");
        testUser.setEmail("test@example.com");
        testUser.setStatus(1);
        testUser.setRole("user");
        testUser.setGrowthPoints(100);
    }

    @Test
    void testFindByUsername_Success() {
        // Arrange
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        // Act
        User result = userService.findByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    void testFindByUsername_NotFound() {
        // Arrange
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // Act
        User result = userService.findByUsername("nonexistent");

        // Assert
        assertNull(result);
    }

    @Test
    void testLogin_Success() {
        // Arrange
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        testUser.setPassword(encoder.encode("password123"));

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("mock-jwt-token");

        // Act
        Map<String, Object> result = userService.login("testuser", "password123");

        // Assert
        assertNotNull(result);
        assertNotNull(result.get("token"));
        assertNotNull(result.get("user"));
        verify(jwtUtil, times(1)).generateToken(anyLong(), anyString());
    }

    @Test
    void testLogin_UserNotFound() {
        // Arrange
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.login("nonexistent", "password"));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testLogin_InvalidPassword() {
        // Arrange
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        testUser.setPassword(encoder.encode("correctpassword"));

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.login("testuser", "wrongpassword"));

        assertEquals("Invalid password", exception.getMessage());
    }

    @Test
    void testLogin_DisabledUser() {
        // Arrange
        testUser.setStatus(0); // Disabled

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.login("testuser", "password123"));

        assertEquals("User account is disabled", exception.getMessage());
    }

    @Test
    void testVerifyPassword_Correct() {
        // Arrange
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("password123");

        // Act
        boolean result = userService.verifyPassword("password123", encodedPassword);

        // Assert
        assertTrue(result);
    }

    @Test
    void testVerifyPassword_Incorrect() {
        // Arrange
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("password123");

        // Act
        boolean result = userService.verifyPassword("wrongpassword", encodedPassword);

        // Assert
        assertFalse(result);
    }

    @Test
    void testUpdateGrowthPoints_Success() {
        // Arrange
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // Act
        userService.updateGrowthPoints(1L, 50);

        // Assert
        verify(userMapper, times(1)).selectById(1L);
        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    void testUpdateGrowthPoints_UserNotFound() {
        // Arrange
        when(userMapper.selectById(999L)).thenReturn(null);

        // Act
        userService.updateGrowthPoints(999L, 50);

        // Assert - should not throw exception, just do nothing
        verify(userMapper, times(1)).selectById(999L);
        verify(userMapper, never()).updateById(any(User.class));
    }
}
