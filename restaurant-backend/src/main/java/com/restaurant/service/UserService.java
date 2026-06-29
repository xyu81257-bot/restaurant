package com.restaurant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.dto.LoginDTO;
import com.restaurant.entity.User;

public interface UserService extends IService<User> {

    /**
     * 用户登录
     */
    String login(LoginDTO loginDTO);

    /**
     * 用户注册（角色默认为 customer）
     */
    void register(User user);

    /**
     * 根据用户名查询用户
     */
    User getUserByUsername(String username);

    /**
     * 根据token获取用户信息
     */
    User getUserInfo(String token);
}