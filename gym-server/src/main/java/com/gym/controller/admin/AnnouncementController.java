package com.gym.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.PageResult;
import com.gym.common.Result;
import com.gym.entity.Announcement;
import com.gym.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("/list")
    public Result<PageResult<Announcement>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        Page<Announcement> pageParam = new Page<>(page, pageSize);
        IPage<Announcement> pageResult = announcementService.getAnnouncementList(pageParam, keyword, type, status);
        return Result.success(new PageResult<>(pageResult.getTotal(), pageResult.getRecords()));
    }

    @GetMapping("/{id}")
    public Result<Announcement> detail(@PathVariable Long id) {
        return Result.success(announcementService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Announcement announcement) {
        announcementService.save(announcement);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Announcement announcement) {
        announcementService.updateById(announcement);
        return Result.success();
    }

    @PutMapping("/publish/{id}")
    public Result<Void> publish(@PathVariable Long id) {
        Announcement announcement = new Announcement();
        announcement.setId(id);
        announcement.setStatus(1);
        announcementService.updateById(announcement);
        return Result.success();
    }

    @PutMapping("/top/{id}")
    public Result<Void> toggleTop(@PathVariable Long id) {
        Announcement existing = announcementService.getById(id);
        Announcement announcement = new Announcement();
        announcement.setId(id);
        announcement.setIsTop(existing.getIsTop() == 1 ? 0 : 1);
        announcementService.updateById(announcement);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        announcementService.removeById(id);
        return Result.success();
    }
}
