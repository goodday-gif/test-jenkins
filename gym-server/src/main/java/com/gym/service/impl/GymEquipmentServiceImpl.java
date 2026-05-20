package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.GymEquipment;
import com.gym.mapper.GymEquipmentMapper;
import com.gym.service.GymEquipmentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class GymEquipmentServiceImpl extends ServiceImpl<GymEquipmentMapper, GymEquipment> implements GymEquipmentService {

    @Override
    public IPage<GymEquipment> getEquipmentList(Page<GymEquipment> page, String keyword, String category, Integer status) {
        LambdaQueryWrapper<GymEquipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), GymEquipment::getName, keyword);
        wrapper.eq(StringUtils.hasText(category), GymEquipment::getCategory, category);
        wrapper.eq(status != null, GymEquipment::getStatus, status);
        wrapper.orderByDesc(GymEquipment::getCreateTime);
        return page(page, wrapper);
    }
}
