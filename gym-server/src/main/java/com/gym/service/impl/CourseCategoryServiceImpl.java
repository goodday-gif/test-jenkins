package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.CourseCategory;
import com.gym.mapper.CourseCategoryMapper;
import com.gym.service.CourseCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory> implements CourseCategoryService {

    @Override
    public List<CourseCategory> listAll() {
        LambdaQueryWrapper<CourseCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(CourseCategory::getSort);
        return list(wrapper);
    }

    @Override
    public void saveCategoryWithSort(CourseCategory category) {
        if (category.getSort() == null) {
            // 自动设置排序值为当前最大值+1
            LambdaQueryWrapper<CourseCategory> wrapper = new LambdaQueryWrapper<>();
            wrapper.orderByDesc(CourseCategory::getSort).last("LIMIT 1");
            CourseCategory last = getOne(wrapper);
            category.setSort(last != null ? last.getSort() + 1 : 1);
        }
        save(category);
    }
}
