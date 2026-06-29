package com.restaurant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService extends IService<OrderDetail> {

    /**
     * 根据订单ID查询明细列表
     */
    List<OrderDetail> listByOrderId(Long orderId);
}