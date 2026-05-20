package com.gym.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.PageResult;
import com.gym.common.Result;
import com.gym.entity.FitnessCourse;
import com.gym.service.FitnessCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/fitness-course")
public class FitnessCourseController {

    @Autowired
    private FitnessCourseService fitnessCourseService;

    @GetMapping("/list")
    public Result<PageResult<FitnessCourse>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer publishStatus) {
        Page<FitnessCourse> pageParam = new Page<>(page, pageSize);
        IPage<FitnessCourse> pageResult = fitnessCourseService.getCourseList(pageParam, keyword, categoryId, publishStatus);
        return Result.success(new PageResult<>(pageResult.getTotal(), pageResult.getRecords()));
    }

    @GetMapping("/{id}")
    public Result<FitnessCourse> detail(@PathVariable Long id) {
        return Result.success(fitnessCourseService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody FitnessCourse course) {
        fitnessCourseService.save(course);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody FitnessCourse course) {
        fitnessCourseService.updateById(course);
        return Result.success();
    }

    @PutMapping("/publish/{id}")
    public Result<Void> publish(@PathVariable Long id) {
        fitnessCourseService.publishCourse(id);
        return Result.success();
    }

    @PutMapping("/offline/{id}")
    public Result<Void> offline(@PathVariable Long id) {
        fitnessCourseService.offlineCourse(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        fitnessCourseService.removeById(id);
        return Result.success();
    }
}
