package com.gym.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.entity.Orders;

public interface OrdersService extends IService<Orders> {

    /**
     * 分页查询订单
     */
    IPage<Orders> getOrderList(Page<Orders> page, Long memberId, Integer orderType, Integer payStatus);

    /**
     * 退款操作
     */
    void refund(Long id);
}
