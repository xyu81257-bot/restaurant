package com.restaurant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.entity.Desk;

public interface DeskService extends IService<Desk> {

    /**
     * 更新桌台状态
     */
    boolean updateDeskStatus(Long deskId, String status);
}