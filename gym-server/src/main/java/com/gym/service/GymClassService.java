package com.gym.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.entity.GymClass;

public interface GymClassService extends IService<GymClass> {

    /**
     * 分页查询课程
     * @param page 分页参数
     * @param keyword 关键词
     * @param classType 课程类型
     * @param status 状态
     * @return 分页结果
     */
    IPage<GymClass> getClassList(Page<GymClass> page, String keyword, Integer classType, Integer status);

    /**
     * 取消课程
     * @param id 课程ID
     */
    void cancelClass(Long id);
}
