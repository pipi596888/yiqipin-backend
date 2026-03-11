package com.yiqipin.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yiqipin.backend.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {

    Map<String, Object> login(String username, String password);

    User findByUsername(String username);

    User register(String username, String password, String phone, String email);

    boolean verifyPassword(String rawPassword, String encodedPassword);

    void updateGrowthPoints(Long userId, int points);
}
