package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.BusinessException;
import com.gym.entity.Orders;
import com.gym.mapper.OrdersMapper;
import com.gym.service.OrdersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Override
    public IPage<Orders> getOrderList(Page<Orders> page, Long memberId, Integer orderType, Integer payStatus) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(memberId != null, Orders::getMemberId, memberId);
        wrapper.eq(orderType != null, Orders::getOrderType, orderType);
        wrapper.eq(payStatus != null, Orders::getPayStatus, payStatus);
        wrapper.orderByDesc(Orders::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refund(Long id) {
        Orders order = getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getPayStatus() != 1) {
            throw new BusinessException("只有已支付的订单才能退款");
        }
        order.setPayStatus(2);
        updateById(order);
    }
}
