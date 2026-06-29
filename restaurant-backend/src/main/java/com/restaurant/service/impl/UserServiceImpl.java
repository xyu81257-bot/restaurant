package com.restaurant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.dto.LoginDTO;
import com.restaurant.entity.User;
import com.restaurant.mapper.UserMapper;
import com.restaurant.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Value("${jwt.secret:restaurant2026SecretKey}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private Long jwtExpiration;

    @Override
    public String login(LoginDTO loginDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername())
                .eq(User::getDeleted, 0);
        User user = this.getOne(wrapper);

        if (user == null) {
            throw new IllegalArgumentException("用户名不存在");
        }

        String encryptedPassword = DigestUtils.md5DigestAsHex(
                loginDTO.getPassword().getBytes(StandardCharsets.UTF_8)
        );

        if (!encryptedPassword.equals(user.getPassword())) {
            throw new IllegalArgumentException("密码错误");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes())
                .compact();

        log.info("用户登录成功: {}，角色: {}", user.getUsername(), user.getRole());
        return token;
    }

    @Override
    public void register(User user) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername())
                .eq(User::getDeleted, 0);
        if (this.getOne(wrapper) != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        String encrypted = DigestUtils.md5DigestAsHex(
                user.getPassword().getBytes(StandardCharsets.UTF_8)
        );
        user.setPassword(encrypted);
        // 默认角色为顾客
        user.setRole("customer");
        user.setDeleted(0);
        this.save(user);
        log.info("用户注册成功: {}", user.getUsername());
    }

    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
                .eq(User::getDeleted, 0);
        return this.getOne(wrapper);
    }

    @Override
    public User getUserInfo(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret.getBytes())
                    .parseClaimsJws(token)
                    .getBody();
            Long userId = claims.get("userId", Long.class);
            return this.getById(userId);
        } catch (Exception e) {
            throw new IllegalArgumentException("无效的token");
        }
    }
}