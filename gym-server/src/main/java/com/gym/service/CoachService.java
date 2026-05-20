package com.gym.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.entity.Coach;

public interface CoachService extends IService<Coach> {

    /**
     * 分页查询教练
     * @param page 分页参数
     * @param keyword 关键词（姓名/手机号）
     * @param status 状态
     * @return 分页结果
     */
    IPage<Coach> getCoachList(Page<Coach> page, String keyword, Integer status);
}
