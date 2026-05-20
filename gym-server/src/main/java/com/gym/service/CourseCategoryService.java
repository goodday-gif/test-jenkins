package com.gym.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.entity.CourseCategory;

import java.util.List;

public interface CourseCategoryService extends IService<CourseCategory> {

    /**
     * 获取所有分类
     */
    List<CourseCategory> listAll();

    /**
     * 新增分类（自动设置排序值）
     */
    void saveCategoryWithSort(CourseCategory category);
}
