package com.gym.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.Result;
import com.gym.entity.Coach;
import com.gym.service.CoachService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/admin/coach")
public class CoachController {

    @Resource
    private CoachService coachService;

    @GetMapping("/list")
    public Result<IPage<Coach>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        Page<Coach> page = new Page<>(current, size);
        IPage<Coach> result = coachService.getCoachList(page, keyword, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Coach> getById(@PathVariable Long id) {
        Coach coach = coachService.getById(id);
        return Result.success(coach);
    }

    @PostMapping
    public Result<Void> add(@RequestBody Coach coach) {
        coachService.save(coach);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Coach coach) {
        coachService.updateById(coach);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        coachService.removeById(id);
        return Result.success();
    }
}
