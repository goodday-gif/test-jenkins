package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.entity.Announcement;
import com.gym.mapper.AnnouncementMapper;
import com.gym.service.AnnouncementService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    @Override
    public IPage<Announcement> getAnnouncementList(Page<Announcement> page, String keyword, Integer type, Integer status) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), Announcement::getTitle, keyword);
        wrapper.eq(type != null, Announcement::getType, type);
        wrapper.eq(status != null, Announcement::getStatus, status);
        wrapper.orderByDesc(Announcement::getIsTop)
                .orderByDesc(Announcement::getCreateTime);
        return page(page, wrapper);
    }
}
