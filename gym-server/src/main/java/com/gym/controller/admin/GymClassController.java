package com.gym.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.Result;
import com.gym.entity.GymClass;
import com.gym.service.GymClassService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/admin/gym-class")
public class GymClassController {

    @Resource
    private GymClassService gymClassService;

    @GetMapping("/list")
    public Result<IPage<GymClass>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer classType,
            @RequestParam(required = false) Integer status) {
        Page<GymClass> page = new Page<>(current, size);
        IPage<GymClass> result = gymClassService.getClassList(page, keyword, classType, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<GymClass> getById(@PathVariable Long id) {
        GymClass gymClass = gymClassService.getById(id);
        return Result.success(gymClass);
    }

    @PostMapping
    public Result<Void> add(@RequestBody GymClass gymClass) {
        gymClassService.save(gymClass);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody GymClass gymClass) {
        gymClassService.updateById(gymClass);
        return Result.success();
    }

    @PutMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        gymClassService.cancelClass(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        gymClassService.removeById(id);
        return Result.success();
    }
}
