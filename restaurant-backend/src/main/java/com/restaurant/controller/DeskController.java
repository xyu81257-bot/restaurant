package com.restaurant.controller;

import com.restaurant.common.Result;
import com.restaurant.entity.Desk;
import com.restaurant.service.DeskService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/desk")   // 添加 /api
public class DeskController {

    @Resource
    private DeskService deskService;

    @GetMapping
    public Result<List<Desk>> list() {
        return Result.success(deskService.list());
    }

    @PostMapping
    public Result<?> add(@RequestBody Desk desk) {
        deskService.save(desk);
        return Result.success(null);
    }

    @PutMapping
    public Result<?> update(@RequestBody Desk desk) {
        boolean success = deskService.updateDeskStatus(desk.getId(), desk.getStatus());
        if (success) {
            return Result.success(null);
        } else {
            return Result.error("更新桌台状态失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        deskService.removeById(id);
        return Result.success(null);
    }
}