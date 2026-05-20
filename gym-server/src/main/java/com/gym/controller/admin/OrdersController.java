package com.gym.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.PageResult;
import com.gym.common.Result;
import com.gym.entity.Orders;
import com.gym.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @GetMapping("/list")
    public Result<PageResult<Orders>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Integer orderType,
            @RequestParam(required = false) Integer payStatus) {
        Page<Orders> pageParam = new Page<>(page, pageSize);
        IPage<Orders> pageResult = ordersService.getOrderList(pageParam, memberId, orderType, payStatus);
        return Result.success(new PageResult<>(pageResult.getTotal(), pageResult.getRecords()));
    }

    @GetMapping("/{id}")
    public Result<Orders> detail(@PathVariable Long id) {
        return Result.success(ordersService.getById(id));
    }

    @PutMapping("/refund/{id}")
    public Result<Void> refund(@PathVariable Long id) {
        ordersService.refund(id);
        return Result.success();
    }

    @PutMapping("/status/{id}")
    public Result<Void> updatePayStatus(@PathVariable Long id, @RequestParam Integer payStatus) {
        Orders order = new Orders();
        order.setId(id);
        order.setPayStatus(payStatus);
        ordersService.updateById(order);
        return Result.success();
    }
}
