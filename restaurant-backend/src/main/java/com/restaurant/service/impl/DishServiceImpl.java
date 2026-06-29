package com.restaurant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.common.PageResult;
import com.restaurant.dto.DishQueryDTO;
import com.restaurant.entity.Dish;
import com.restaurant.mapper.DishMapper;
import com.restaurant.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Override
    public PageResult<Dish> queryDishList(DishQueryDTO queryDTO) {
        Page<Dish> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getDeleted, 0);

        if (queryDTO.getName() != null && !queryDTO.getName().isEmpty()) {
            wrapper.like(Dish::getDishName, queryDTO.getName());
        }
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(Dish::getCategoryId, queryDTO.getCategoryId());
        }

        Page<Dish> result = this.page(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    public boolean updateStock(Long dishId, Integer quantity) {
        Dish dish = this.getById(dishId);
        if (dish == null) {
            return false;
        }
        dish.setStock(quantity);
        return this.updateById(dish);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        return this.updateById(dish);
    }
}