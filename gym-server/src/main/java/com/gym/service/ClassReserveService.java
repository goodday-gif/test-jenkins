package com.gym.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.entity.ClassReserve;

public interface ClassReserveService extends IService<ClassReserve> {

    /**
     * 分页查询预约记录
     * @param page 分页参数
     * @param classId 课程ID
     * @param memberId 会员ID
     * @param status 状态
     * @return 分页结果
     */
    IPage<ClassReserve> getReserveList(Page<ClassReserve> page, Long classId, Long memberId, Integer status);

    /**
     * 签到/核销
     * @param id 预约ID
     */
    void checkIn(Long id);

    /**
     * 取消预约
     * @param id 预约ID
     */
    void cancelReserve(Long id);
}
