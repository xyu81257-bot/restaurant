package com.restaurant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long userId;
    private Long deskId;
    private BigDecimal totalAmount;
    private String orderStatus;
    private String payStatus;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private Integer deleted;

    @TableField(exist = false)
    private List<OrderDetail> details;
}