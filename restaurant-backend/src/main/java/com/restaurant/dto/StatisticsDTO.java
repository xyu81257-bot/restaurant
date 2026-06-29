package com.restaurant.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatisticsDTO {

    private BigDecimal todayIncome;
    private Long todayOrders;
    private BigDecimal monthIncome;
    private Long monthOrders;
    private Long totalOrders;
}