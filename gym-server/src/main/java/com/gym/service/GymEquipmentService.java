package com.gym.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.entity.GymEquipment;

public interface GymEquipmentService extends IService<GymEquipment> {

    /**
     * 分页查询器材
     */
    IPage<GymEquipment> getEquipmentList(Page<GymEquipment> page, String keyword, String category, Integer status);
}
