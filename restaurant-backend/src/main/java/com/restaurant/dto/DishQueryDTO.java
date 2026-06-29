package com.restaurant.dto;

import lombok.Data;

@Data
public class DishQueryDTO {

    private String name;
    private Long categoryId;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}