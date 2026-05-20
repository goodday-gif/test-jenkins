package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.BusinessException;
import com.gym.entity.FitnessCourse;
import com.gym.mapper.FitnessCourseMapper;
import com.gym.service.FitnessCourseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class FitnessCourseServiceImpl extends ServiceImpl<FitnessCourseMapper, FitnessCourse> implements FitnessCourseService {

    @Override
    public IPage<FitnessCourse> getCourseList(Page<FitnessCourse> page, String keyword, Long categoryId, Integer publishStatus) {
        LambdaQueryWrapper<FitnessCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), FitnessCourse::getTitle, keyword);
        wrapper.eq(categoryId != null, FitnessCourse::getCategoryId, categoryId);
        wrapper.eq(publishStatus != null, FitnessCourse::getPublishStatus, publishStatus);
        wrapper.orderByDesc(FitnessCourse::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public void publishCourse(Long id) {
        FitnessCourse course = getById(id);
        if (course == null) {
            throw new BusinessException("教程不存在");
        }
        course.setPublishStatus(1);
        updateById(course);
    }

    @Override
    public void offlineCourse(Long id) {
        FitnessCourse course = getById(id);
        if (course == null) {
            throw new BusinessException("教程不存在");
        }
        course.setPublishStatus(2);
        updateById(course);
    }
}
