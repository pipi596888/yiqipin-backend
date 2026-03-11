package com.yiqipin.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yiqipin.backend.entity.Address;
import com.yiqipin.backend.mapper.AddressMapper;
import com.yiqipin.backend.service.impl.AddressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressMapper addressMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    private Address testAddress;

    @BeforeEach
    void setUp() {
        testAddress = new Address();
        testAddress.setId(1L);
        testAddress.setUserId(1L);
        testAddress.setReceiver("Test User");
        testAddress.setPhone("13800138000");
        testAddress.setProvince("Guangdong");
        testAddress.setCity("Shenzhen");
        testAddress.setDistrict("Nanshan");
        testAddress.setDetail("Test Address");
        testAddress.setIsDefault(1);
    }

    @Test
    void testListByUserId_Success() {
        // Arrange
        List<Address> addresses = Arrays.asList(testAddress);
        when(addressMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(addresses);

        // Act
        List<Address> result = addressService.listByUserId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test User", result.get(0).getReceiver());
    }

    @Test
    void testListByUserId_Empty() {
        // Arrange
        when(addressMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        // Act
        List<Address> result = addressService.listByUserId(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetDefaultAddress_Success() {
        // Arrange
        when(addressMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testAddress);

        // Act
        Address result = addressService.getDefaultAddress(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getIsDefault());
    }

    @Test
    void testGetDefaultAddress_NoDefault() {
        // Arrange
        when(addressMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // Act
        Address result = addressService.getDefaultAddress(1L);

        // Assert
        assertNull(result);
    }

    @Test
    void testSetDefault_Success() {
        // Arrange
        when(addressMapper.update(any(), any(LambdaQueryWrapper.class))).thenReturn(1);
        when(addressMapper.selectById(1L)).thenReturn(testAddress);
        when(addressMapper.updateById(any(Address.class))).thenReturn(1);

        // Act
        addressService.setDefault(1L, 1L);

        // Assert
        verify(addressMapper, times(1)).update(any(), any(LambdaQueryWrapper.class));
        verify(addressMapper, times(1)).updateById(any(Address.class));
    }

    @Test
    void testSetDefault_AddressNotFound() {
        // Arrange
        when(addressMapper.update(any(), any(LambdaQueryWrapper.class))).thenReturn(1);
        when(addressMapper.selectById(999L)).thenReturn(null);

        // Act
        addressService.setDefault(1L, 999L);

        // Assert
        verify(addressMapper, times(1)).update(any(), any(LambdaQueryWrapper.class));
        verify(addressMapper, never()).updateById(any(Address.class));
    }

    @Test
    void testSetDefault_WrongUser() {
        // Arrange
        testAddress.setUserId(2L); // Different user
        when(addressMapper.update(any(), any(LambdaQueryWrapper.class))).thenReturn(1);
        when(addressMapper.selectById(1L)).thenReturn(testAddress);

        // Act
        addressService.setDefault(1L, 1L);

        // Assert
        verify(addressMapper, times(1)).update(any(), any(LambdaQueryWrapper.class));
        verify(addressMapper, never()).updateById(any(Address.class));
    }
}
