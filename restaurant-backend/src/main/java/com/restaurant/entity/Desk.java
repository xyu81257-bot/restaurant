package com.restaurant.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("desk")
public class Desk {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String number;
    private String status;
    private Integer deleted;
}