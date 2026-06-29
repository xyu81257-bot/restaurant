package com.restaurant.controller;

import com.restaurant.common.Result;
import com.restaurant.entity.Category;
import com.restaurant.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/category")   // 添加 /api
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryService.list());
    }

    @PostMapping
    public Result<?> add(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success(null);
    }

    @PutMapping
    public Result<?> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success(null);
    }
}