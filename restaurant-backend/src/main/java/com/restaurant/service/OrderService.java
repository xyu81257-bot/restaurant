package com.restaurant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.common.PageResult;
import com.restaurant.dto.OrderDTO;
import com.restaurant.dto.StatisticsDTO;
import com.restaurant.entity.Order;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     */
    Order createOrder(OrderDTO orderDTO);

    /**
     * 分页查询订单列表
     */
    PageResult<Order> queryOrderList(Integer pageNum, Integer pageSize, String orderNo, String orderStatus, Long deskId);

    /**
     * 更新订单状态
     */
    boolean updateOrderStatus(Long orderId, String orderStatus);

    /**
     * 订单支付
     */
    boolean payOrder(Long orderId);

    /**
     * 取消订单
     */
    boolean cancelOrder(Long orderId);

    /**
     * 获取订单详情（含明细）
     */
    Order getOrderDetail(Long orderId);

    /**
     * 获取统计数据
     */
    StatisticsDTO getStatistics();

    /**
     * 获取热销菜品排行
     */
    List<Map<String, Object>> getHotDishList(Integer topN);

    /**
     * 获取日营业额趋势
     */
    List<Map<String, Object>> getDailyIncomeTrend(LocalDate startDate, LocalDate endDate);
}