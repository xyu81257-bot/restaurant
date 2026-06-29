package com.restaurant.controller;

import com.restaurant.common.Result;
import com.restaurant.entity.Dish;
import com.restaurant.service.DishService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/dish")   // 添加 /api
public class DishController {

    @Resource
    private DishService dishService;

    @GetMapping
    public Result<List<Dish>> list() {
        return Result.success(dishService.list());
    }

    @PostMapping
    public Result<?> add(@RequestBody Dish dish) {
        dishService.save(dish);
        return Result.success(null);
    }

    @PutMapping
    public Result<?> update(@RequestBody Dish dish) {
        dishService.updateById(dish);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        dishService.removeById(id);
        return Result.success(null);
    }
}