package com.restaurant.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {

    private Long id;
    private Long deskId;
    private String deskNumber;
    private Long userId;
    private List<OrderDetailDTO> details;
}