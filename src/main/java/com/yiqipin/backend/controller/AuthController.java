package com.yiqipin.backend.controller;

import com.yiqipin.backend.common.ApiResponse;
import com.yiqipin.backend.dto.LoginRequest;
import com.yiqipin.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Map<String, Object> result = userService.login(request.username(), request.password());
            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            String phone = request.get("phone");
            String email = request.get("email");

            if (username == null || password == null) {
                return ApiResponse.fail("Username and password are required");
            }

            userService.register(username, password, phone, email);
            return ApiResponse.success(Map.of("message", "Registration successful"));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}
