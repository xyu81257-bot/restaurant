package com.restaurant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.common.PageResult;
import com.restaurant.dto.OrderDTO;
import com.restaurant.dto.OrderDetailDTO;
import com.restaurant.dto.StatisticsDTO;
import com.restaurant.entity.*;
import com.restaurant.mapper.OrderMapper;
import com.restaurant.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderDetailService orderDetailService;
    private final DishService dishService;
    private final DeskService deskService;
    private final InventoryService inventoryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(OrderDTO orderDTO) {
        String orderNo = generateOrderNo();

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderDetail> details = new ArrayList<>();

        for (OrderDetailDTO detailDTO : orderDTO.getDetails()) {
            Dish dish = dishService.getById(detailDTO.getDishId());
            if (dish == null) {
                throw new IllegalArgumentException("菜品不存在: " + detailDTO.getDishId());
            }
            if (dish.getStatus() != 1) {
                throw new IllegalArgumentException("菜品已下架: " + dish.getDishName());
            }

            if (!inventoryService.checkStock(detailDTO.getDishId(), detailDTO.getCount())) {
                throw new IllegalArgumentException("库存不足: " + dish.getDishName());
            }

            BigDecimal amount = dish.getPrice().multiply(BigDecimal.valueOf(detailDTO.getCount()));
            totalAmount = totalAmount.add(amount);

            OrderDetail detail = new OrderDetail();
            detail.setDishId(detailDTO.getDishId());
            detail.setQuantity(detailDTO.getCount());
            detail.setPrice(dish.getPrice());
            detail.setAmount(amount);
            details.add(detail);
        }

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(orderDTO.getUserId());
        order.setDeskId(orderDTO.getDeskId());
        order.setTotalAmount(totalAmount);
        order.setOrderStatus("待支付");
        order.setPayStatus("未支付");
        order.setCreateTime(LocalDateTime.now());
        this.save(order);

        for (OrderDetail detail : details) {
            detail.setOrderId(order.getId());
            orderDetailService.save(detail);
        }

        deskService.updateDeskStatus(orderDTO.getDeskId(), "已占用");

        for (OrderDetailDTO detailDTO : orderDTO.getDetails()) {
            inventoryService.deductStock(detailDTO.getDishId(), detailDTO.getCount());
        }

        log.info("订单创建成功: orderNo={}, totalAmount={}", orderNo, totalAmount);
        return order;
    }

    @Override
    public PageResult<Order> queryOrderList(Integer pageNum, Integer pageSize, String orderNo, String orderStatus, Long deskId) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getDeleted, 0);

        if (orderNo != null && !orderNo.isEmpty()) {
            wrapper.like(Order::getOrderNo, orderNo);
        }
        if (orderStatus != null && !orderStatus.isEmpty()) {
            wrapper.eq(Order::getOrderStatus, orderStatus);
        }
        if (deskId != null) {
            wrapper.eq(Order::getDeskId, deskId);
        }
        wrapper.orderByDesc(Order::getCreateTime);

        Page<Order> result = this.page(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    public boolean updateOrderStatus(Long orderId, String orderStatus) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        if ("已完成".equals(orderStatus) || "已取消".equals(orderStatus)) {
            deskService.updateDeskStatus(order.getDeskId(), "空闲");
        }
        order.setOrderStatus(orderStatus);
        return this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        if ("已支付".equals(order.getPayStatus())) {
            throw new IllegalArgumentException("订单已支付");
        }
        if (!"待支付".equals(order.getOrderStatus())) {
            throw new IllegalArgumentException("当前订单状态不支持支付");
        }

        order.setPayStatus("已支付");
        order.setOrderStatus("已支付");
        order.setPayTime(LocalDateTime.now());
        return this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        if ("已完成".equals(order.getOrderStatus()) || "已取消".equals(order.getOrderStatus())) {
            throw new IllegalArgumentException("订单已完成或已取消，无法操作");
        }

        if ("已支付".equals(order.getPayStatus())) {
            List<OrderDetail> details = orderDetailService.listByOrderId(orderId);
            for (OrderDetail detail : details) {
                inventoryService.addStock(detail.getDishId(), detail.getQuantity());
            }
        }

        order.setOrderStatus("已取消");
        return this.updateById(order);
    }

    @Override
    public Order getOrderDetail(Long orderId) {
        return this.getById(orderId);
    }

    @Override
    public StatisticsDTO getStatistics() {
        StatisticsDTO dto = new StatisticsDTO();

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);
        LambdaQueryWrapper<Order> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(Order::getPayStatus, "已支付")
                .eq(Order::getDeleted, 0)
                .between(Order::getPayTime, todayStart, todayEnd);
        List<Order> todayOrders = this.list(todayWrapper);
        BigDecimal todayIncome = todayOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTodayIncome(todayIncome);
        dto.setTodayOrders((long) todayOrders.size());

        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LambdaQueryWrapper<Order> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.eq(Order::getPayStatus, "已支付")
                .eq(Order::getDeleted, 0)
                .between(Order::getPayTime, monthStart, todayEnd);
        List<Order> monthOrders = this.list(monthWrapper);
        BigDecimal monthIncome = monthOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setMonthIncome(monthIncome);
        dto.setMonthOrders((long) monthOrders.size());

        LambdaQueryWrapper<Order> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(Order::getDeleted, 0);
        dto.setTotalOrders(this.count(totalWrapper));

        return dto;
    }

    @Override
    public List<Map<String, Object>> getHotDishList(Integer topN) {
        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(Order::getPayStatus, "已支付")
                .eq(Order::getDeleted, 0);
        List<Order> orders = this.list(orderWrapper);
        List<Long> orderIds = orders.stream().map(Order::getId).collect(Collectors.toList());

        if (orderIds.isEmpty()) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<OrderDetail> detailWrapper = new LambdaQueryWrapper<>();
        detailWrapper.in(OrderDetail::getOrderId, orderIds)
                .eq(OrderDetail::getDeleted, 0);
        List<OrderDetail> details = orderDetailService.list(detailWrapper);

        Map<Long, Integer> dishSaleMap = new HashMap<>();
        for (OrderDetail detail : details) {
            dishSaleMap.put(detail.getDishId(),
                    dishSaleMap.getOrDefault(detail.getDishId(), 0) + detail.getQuantity());
        }

        List<Map.Entry<Long, Integer>> sorted = dishSaleMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue() - e1.getValue())
                .limit(topN)
                .collect(Collectors.toList());

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : sorted) {
            Dish dish = dishService.getById(entry.getKey());
            if (dish != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("dishId", dish.getId());
                map.put("dishName", dish.getDishName());
                map.put("totalSales", entry.getValue());
                result.add(map);
            }
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getDailyIncomeTrend(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> result = new ArrayList<>();

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getPayStatus, "已支付")
                .eq(Order::getDeleted, 0)
                .between(Order::getPayTime, startDate.atStartOfDay(), endDate.atTime(23, 59, 59))
                .orderByAsc(Order::getPayTime);

        List<Order> orders = this.list(wrapper);

        Map<String, BigDecimal> dailyMap = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Order order : orders) {
            if (order.getPayTime() != null) {
                String dateKey = order.getPayTime().format(formatter);
                dailyMap.put(dateKey,
                        dailyMap.getOrDefault(dateKey, BigDecimal.ZERO).add(order.getTotalAmount()));
            }
        }

        for (Map.Entry<String, BigDecimal> entry : dailyMap.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", entry.getKey());
            map.put("income", entry.getValue());
            result.add(map);
        }

        return result;
    }

    private String generateOrderNo() {
        return "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", new Random().nextInt(10000));
    }
}