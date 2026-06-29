package com.restaurant.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailDTO {

    private Long dishId;
    private String dishName;
    private BigDecimal price;
    private Integer count;
    private BigDecimal amount;
}