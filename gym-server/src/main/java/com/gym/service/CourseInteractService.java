package com.gym.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.entity.CourseInteract;

public interface CourseInteractService extends IService<CourseInteract> {

    /**
     * 分页查询评论列表
     */
    IPage<CourseInteract> getCommentList(Page<CourseInteract> page, Long courseId);
}
