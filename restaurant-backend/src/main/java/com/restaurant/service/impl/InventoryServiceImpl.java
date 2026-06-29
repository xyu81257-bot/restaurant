package com.restaurant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.entity.Inventory;
import com.restaurant.mapper.InventoryMapper;
import com.restaurant.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements InventoryService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStock(Long dishId, Integer quantity) {
        Inventory inventory = getInventoryByDishId(dishId);
        if (inventory == null) {
            log.warn("库存记录不存在: dishId={}", dishId);
            return false;
        }
        if (inventory.getQuantity() < quantity) {
            log.warn("库存不足: dishId={}, current={}, need={}", dishId, inventory.getQuantity(), quantity);
            return false;
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventory.setUpdateTime(LocalDateTime.now());
        return this.updateById(inventory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addStock(Long dishId, Integer quantity) {
        Inventory inventory = getInventoryByDishId(dishId);
        if (inventory == null) {
            inventory = new Inventory();
            inventory.setDishId(dishId);
            inventory.setQuantity(quantity);
            inventory.setWarningLine(10);
            inventory.setUpdateTime(LocalDateTime.now());
            return this.save(inventory);
        }

        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventory.setUpdateTime(LocalDateTime.now());
        return this.updateById(inventory);
    }

    @Override
    public Inventory getInventoryByDishId(Long dishId) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getDishId, dishId)
                .eq(Inventory::getDeleted, 0);
        return this.getOne(wrapper);
    }

    @Override
    public boolean checkStock(Long dishId, Integer quantity) {
        Inventory inventory = getInventoryByDishId(dishId);
        if (inventory == null) {
            return false;
        }
        return inventory.getQuantity() >= quantity;
    }

    @Override
    public List<Inventory> getWarningList() {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        // 修复：使用 le 方法，第一个参数是字段，第二个参数是具体值
        // 查询 quantity <= warning_line 的记录
        wrapper.apply("quantity <= warning_line")
                .eq(Inventory::getDeleted, 0);
        return this.list(wrapper);
    }
}