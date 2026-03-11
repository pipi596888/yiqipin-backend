package com.yiqipin.backend.controller;

import com.yiqipin.backend.common.ApiResponse;
import com.yiqipin.backend.entity.Address;
import com.yiqipin.backend.entity.User;
import com.yiqipin.backend.service.AddressService;
import com.yiqipin.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final AddressService addressService;

    public UserController(UserService userService, AddressService addressService) {
        this.userService = userService;
        this.addressService = addressService;
    }

    @GetMapping("/profile")
    public ApiResponse<?> profile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null); // Don't return password
        }
        return ApiResponse.success(user);
    }

    @GetMapping("/address")
    public ApiResponse<?> address(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Address> addresses = addressService.listByUserId(userId);
        return ApiResponse.success(addresses);
    }

    @PostMapping("/address")
    public ApiResponse<Address> addAddress(@RequestBody Address address, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        address.setUserId(userId);
        addressService.save(address);
        return ApiResponse.success(address);
    }

    @PutMapping("/address/{id}")
    public ApiResponse<Address> updateAddress(@PathVariable Long id, @RequestBody Address address) {
        address.setId(id);
        addressService.updateById(address);
        return ApiResponse.success(address);
    }

    @DeleteMapping("/address/{id}")
    public ApiResponse<Void> deleteAddress(@PathVariable Long id) {
        addressService.removeById(id);
        return ApiResponse.success(null);
    }
}
