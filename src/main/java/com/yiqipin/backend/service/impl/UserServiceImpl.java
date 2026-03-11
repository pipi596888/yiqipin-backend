package com.yiqipin.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiqipin.backend.entity.GrowthRecord;
import com.yiqipin.backend.entity.User;
import com.yiqipin.backend.mapper.GrowthRecordMapper;
import com.yiqipin.backend.mapper.UserMapper;
import com.yiqipin.backend.security.JwtUtil;
import com.yiqipin.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final GrowthRecordMapper growthRecordMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Map<String, Object> login(String username, String password) {
        User user = findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (user.getStatus() != 1) {
            throw new RuntimeException("User account is disabled");
        }

        if (!verifyPassword(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("phone", user.getPhone());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("email", user.getEmail());
        result.put("user", userInfo);

        return result;
    }

    @Override
    public User findByUsername(String username) {
        return this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
    }

    @Override
    @Transactional
    public User register(String username, String password, String phone, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setEmail(email);
        user.setStatus(1);
        user.setRole("user");
        user.setGrowthPoints(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        this.save(user);

        // Add initial growth points for new user
        GrowthRecord record = new GrowthRecord();
        record.setUserId(user.getId());
        record.setType("register");
        record.setPoints(50);
        record.setDescription("New user registration bonus");
        record.setCreatedAt(LocalDateTime.now());
        growthRecordMapper.insert(record);

        // Update user growth points
        this.updateGrowthPoints(user.getId(), 50);

        return user;
    }

    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    @Transactional
    public void updateGrowthPoints(Long userId, int points) {
        User user = this.getById(userId);
        if (user != null) {
            user.setGrowthPoints(user.getGrowthPoints() + points);
            this.updateById(user);
        }
    }
}
