package com.restaurant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.entity.Inventory;

import java.util.List;

public interface InventoryService extends IService<Inventory> {

    /**
     * 扣减库存
     */
    boolean deductStock(Long dishId, Integer quantity);

    /**
     * 增加库存
     */
    boolean addStock(Long dishId, Integer quantity);

    /**
     * 获取菜品库存
     */
    Inventory getInventoryByDishId(Long dishId);

    /**
     * 检查库存是否充足
     */
    boolean checkStock(Long dishId, Integer quantity);

    /**
     * 获取库存预警列表
     */
    List<Inventory> getWarningList();
}