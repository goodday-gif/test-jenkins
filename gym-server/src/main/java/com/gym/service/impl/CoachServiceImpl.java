package com.gym.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.Coach;
import com.gym.mapper.CoachMapper;
import com.gym.service.CoachService;
import org.springframework.stereotype.Service;

@Service
public class CoachServiceImpl extends ServiceImpl<CoachMapper, Coach> implements CoachService {

    @Override
    public IPage<Coach> getCoachList(Page<Coach> page, String keyword, Integer status) {
        LambdaQueryWrapper<Coach> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(StrUtil.isNotBlank(keyword), w ->
                w.like(Coach::getName, keyword)
                 .or()
                 .like(Coach::getPhone, keyword)
        );
        wrapper.eq(status != null, Coach::getStatus, status);
        wrapper.orderByDesc(Coach::getCreateTime);
        return page(page, wrapper);
    }
}
