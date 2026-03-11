package com.yiqipin.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiqipin.backend.entity.Address;
import com.yiqipin.backend.mapper.AddressMapper;
import com.yiqipin.backend.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    @Cacheable(value = "address", key = "'user_' + #userId")
    public List<Address> listByUserId(Long userId) {
        return this.list(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreatedAt));
    }

    @Override
    @Cacheable(value = "addressDefault", key = "'user_' + #userId")
    public Address getDefaultAddress(Long userId) {
        return this.getOne(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"address", "addressDefault"}, key = "'user_' + #userId")
    public void setDefault(Long userId, Long addressId) {
        // Remove default from all addresses
        this.update(null, new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1));

        // Set new default
        Address address = this.getById(addressId);
        if (address != null && address.getUserId().equals(userId)) {
            address.setIsDefault(1);
            this.updateById(address);
        }
    }
}
