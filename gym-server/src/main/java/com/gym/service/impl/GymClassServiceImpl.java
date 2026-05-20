package com.gym.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.BusinessException;
import com.gym.entity.GymClass;
import com.gym.mapper.GymClassMapper;
import com.gym.service.GymClassService;
import org.springframework.stereotype.Service;

@Service
public class GymClassServiceImpl extends ServiceImpl<GymClassMapper, GymClass> implements GymClassService {

    @Override
    public IPage<GymClass> getClassList(Page<GymClass> page, String keyword, Integer classType, Integer status) {
        LambdaQueryWrapper<GymClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(keyword), GymClass::getName, keyword);
        wrapper.eq(classType != null, GymClass::getClassType, classType);
        wrapper.eq(status != null, GymClass::getStatus, status);
        wrapper.orderByDesc(GymClass::getCreateTime);
        return page(page, wrapper);
    }

    @Override
    public void cancelClass(Long id) {
        GymClass gymClass = getById(id);
        if (gymClass == null) {
            throw new BusinessException("课程不存在");
        }
        if (gymClass.getStatus() == 0) {
            throw new BusinessException("课程已取消，无需重复操作");
        }
        gymClass.setStatus(0);
        updateById(gymClass);
    }
}
