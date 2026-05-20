package com.gym.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.entity.FitnessCourse;

public interface FitnessCourseService extends IService<FitnessCourse> {

    /**
     * 分页查询教程
     */
    IPage<FitnessCourse> getCourseList(Page<FitnessCourse> page, String keyword, Long categoryId, Integer publishStatus);

    /**
     * 发布教程
     */
    void publishCourse(Long id);

    /**
     * 下架教程
     */
    void offlineCourse(Long id);
}
