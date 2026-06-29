package com.restaurant.controller;

import com.restaurant.common.Result;
import com.restaurant.dto.StatisticsDTO;
import com.restaurant.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final OrderService orderService;

    /**
     * 获取营业统计总览
     */
    @GetMapping("/overview")
    public Result<StatisticsDTO> getOverview() {
        StatisticsDTO statistics = orderService.getStatistics();
        return Result.success(statistics);
    }

    /**
     * 获取热销菜品排行
     */
    @GetMapping("/hot-dish")
    public Result<List<Map<String, Object>>> getHotDishList(
            @RequestParam(defaultValue = "5") Integer topN) {
        List<Map<String, Object>> list = orderService.getHotDishList(topN);
        return Result.success(list);
    }

    /**
     * 获取日营业额趋势
     */
    @GetMapping("/daily-trend")
    public Result<List<Map<String, Object>>> getDailyTrend(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<Map<String, Object>> list = orderService.getDailyIncomeTrend(startDate, endDate);
        return Result.success(list);
    }
}