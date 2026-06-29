package com.restaurant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.common.PageResult;
import com.restaurant.dto.DishQueryDTO;
import com.restaurant.entity.Dish;

public interface DishService extends IService<Dish> {

    /**
     * 分页查询菜品
     */
    PageResult<Dish> queryDishList(DishQueryDTO queryDTO);

    /**
     * 更新菜品库存
     */
    boolean updateStock(Long dishId, Integer quantity);

    /**
     * 上下架菜品
     */
    boolean updateStatus(Long id, Integer status);
}