package com.restaurant.controller;

import com.restaurant.common.Result;
import com.restaurant.dto.LoginDTO;
import com.restaurant.entity.User;
import com.restaurant.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        return Result.success(token);
    }

    /**
     * 用户注册（默认角色为 customer）
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        userService.register(user);
        return Result.success(null);
    }

    /**
     * 获取当前用户信息（根据token）
     */
    @GetMapping("/info")
    public Result<User> getInfo(@RequestHeader("Authorization") String token) {
        User user = userService.getUserInfo(token);
        return Result.success(user);
    }

    /**
     * 获取所有用户（仅管理员）
     */
    @GetMapping("/list")
    public Result<List<User>> list() {
        return Result.success(userService.list());
    }

    /**
     * 新增用户（仅管理员）
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody User user) {
        userService.save(user);
        return Result.success(null);
    }

    /**
     * 更新用户（仅管理员）
     */
    @PutMapping("/update")
    public Result<?> update(@RequestBody User user) {
        userService.updateById(user);
        return Result.success(null);
    }

    /**
     * 删除用户（仅管理员）
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success(null);
    }
}