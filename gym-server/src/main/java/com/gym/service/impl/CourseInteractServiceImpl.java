package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.CourseInteract;
import com.gym.mapper.CourseInteractMapper;
import com.gym.service.CourseInteractService;
import org.springframework.stereotype.Service;

@Service
public class CourseInteractServiceImpl extends ServiceImpl<CourseInteractMapper, CourseInteract> implements CourseInteractService {

    @Override
    public IPage<CourseInteract> getCommentList(Page<CourseInteract> page, Long courseId) {
        LambdaQueryWrapper<CourseInteract> wrapper = new LambdaQueryWrapper<>();
        // 只查询评论类型（type=3）
        wrapper.eq(CourseInteract::getType, 3);
        wrapper.eq(courseId != null, CourseInteract::getCourseId, courseId);
        wrapper.orderByDesc(CourseInteract::getCreateTime);
        return page(page, wrapper);
    }
}
