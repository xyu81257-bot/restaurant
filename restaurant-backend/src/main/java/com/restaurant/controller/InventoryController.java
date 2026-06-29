package com.restaurant.controller;

import com.restaurant.common.Result;
import com.restaurant.entity.Inventory;
import com.restaurant.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")   // 添加 /api
public class InventoryController {

    @Resource
    private InventoryService inventoryService;

    @GetMapping
    public Result<List<Inventory>> list() {
        return Result.success(inventoryService.list());
    }

    @PostMapping
    public Result<?> add(@RequestBody Inventory inventory) {
        inventoryService.save(inventory);
        return Result.success(null);
    }

    @PutMapping
    public Result<?> update(@RequestBody Inventory inventory) {
        inventoryService.updateById(inventory);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        inventoryService.removeById(id);
        return Result.success(null);
    }
}