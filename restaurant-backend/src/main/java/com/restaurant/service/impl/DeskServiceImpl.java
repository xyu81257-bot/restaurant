package com.restaurant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.entity.Desk;
import com.restaurant.entity.Order;
import com.restaurant.mapper.DeskMapper;
import com.restaurant.service.DeskService;
import com.restaurant.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeskServiceImpl extends ServiceImpl<DeskMapper, Desk> implements DeskService {

    @Autowired
    @Lazy   // 解决循环依赖
    private OrderService orderService;

    @Override
    public boolean updateDeskStatus(Long deskId, String status) {
        // 如果桌台状态变为“空闲”，自动完成该桌台所有“已支付”的订单
        if ("空闲".equals(status)) {
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getDeskId, deskId)
                    .eq(Order::getOrderStatus, "已支付")
                    .eq(Order::getDeleted, 0);
            List<Order> orders = orderService.list(wrapper);
            for (Order order : orders) {
                // 直接修改订单状态为“已完成”，不触发桌台释放逻辑
                order.setOrderStatus("已完成");
                orderService.updateById(order);
            }
        }

        // 更新桌台状态
        LambdaUpdateWrapper<Desk> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Desk::getId, deskId)
                .eq(Desk::getDeleted, 0)
                .set(Desk::getStatus, status);
        return this.update(wrapper);
    }
}