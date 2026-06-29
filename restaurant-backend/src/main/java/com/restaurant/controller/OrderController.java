package com.restaurant.controller;

import com.restaurant.common.PageResult;
import com.restaurant.common.Result;
import com.restaurant.dto.OrderDTO;
import com.restaurant.entity.Order;
import com.restaurant.entity.OrderDetail;
import com.restaurant.service.OrderDetailService;
import com.restaurant.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderDetailService orderDetailService;

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<Order> createOrder(@RequestBody OrderDTO orderDTO) {
        orderDTO.setUserId(1L);
        Order order = orderService.createOrder(orderDTO);
        return Result.success(order);
    }

    /**
     * 分页查询订单列表
     */
    @GetMapping("/list")
    public Result<PageResult<Order>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) Long deskId) {
        PageResult<Order> result = orderService.queryOrderList(pageNum, pageSize, orderNo, orderStatus, deskId);
        return Result.success(result);
    }

    /**
     * 获取订单详情（含明细）
     */
    @GetMapping("/detail/{orderId}")
    public Result<?> getOrderDetail(@PathVariable Long orderId) {
        Order order = orderService.getOrderDetail(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        List<OrderDetail> details = orderDetailService.listByOrderId(orderId);
        order.setDetails(details);
        return Result.success(order);
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/status/{orderId}")
    public Result<?> updateStatus(
            @PathVariable Long orderId,
            @RequestParam String orderStatus) {
        boolean success = orderService.updateOrderStatus(orderId, orderStatus);
        if (success) {
            return Result.success(null);
        }
        return Result.error("更新失败");
    }

    /**
     * 订单支付
     */
    @PostMapping("/pay/{orderId}")
    public Result<?> payOrder(@PathVariable Long orderId) {
        boolean success = orderService.payOrder(orderId);
        if (success) {
            return Result.success(null);
        }
        return Result.error("支付失败");
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderId}")
    public Result<?> cancelOrder(@PathVariable Long orderId) {
        boolean success = orderService.cancelOrder(orderId);
        if (success) {
            return Result.success(null);
        }
        return Result.error("取消失败");
    }
}