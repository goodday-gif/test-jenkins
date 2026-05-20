package com.gym.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.BusinessException;
import com.gym.common.PageResult;
import com.gym.common.Result;
import com.gym.entity.Announcement;
import com.gym.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("frontAnnouncementController")
@RequestMapping("/api/front/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    /**
     * 公告/资讯列表（分页，支持 type 筛选，只返回已发布的）
     */
    @GetMapping("/list")
    public Result<PageResult<Announcement>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, 1); // 只返回已发布
        wrapper.eq(type != null, Announcement::getType, type);
        wrapper.orderByDesc(Announcement::getIsTop)
               .orderByDesc(Announcement::getCreateTime);
        Page<Announcement> page = new Page<>(pageNum, pageSize);
        IPage<Announcement> result = announcementService.page(page, wrapper);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 公告/资讯详情
     */
    @GetMapping("/{id}")
    public Result<Announcement> detail(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        if (announcement == null || announcement.getStatus() != 1) {
            throw new BusinessException("公告不存在或未发布");
        }
        return Result.success(announcement);
    }
}
