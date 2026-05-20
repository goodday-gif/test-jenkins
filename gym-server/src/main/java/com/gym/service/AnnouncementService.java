package com.gym.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.entity.Announcement;

public interface AnnouncementService extends IService<Announcement> {

    /**
     * 分页查询公告资讯
     */
    IPage<Announcement> getAnnouncementList(Page<Announcement> page, String keyword, Integer type, Integer status);
}
