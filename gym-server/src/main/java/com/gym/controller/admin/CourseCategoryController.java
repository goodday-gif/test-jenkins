package com.gym.controller.admin;

import com.gym.common.Result;
import com.gym.entity.CourseCategory;
import com.gym.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/category")
public class CourseCategoryController {

    @Autowired
    private CourseCategoryService courseCategoryService;

    @GetMapping("/list")
    public Result<List<CourseCategory>> list() {
        return Result.success(courseCategoryService.listAll());
    }

    @PostMapping
    public Result<Void> add(@RequestBody CourseCategory category) {
        courseCategoryService.saveCategoryWithSort(category);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody CourseCategory category) {
        courseCategoryService.updateById(category);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        courseCategoryService.removeById(id);
        return Result.success();
    }

    @PutMapping("/sort")
    public Result<Void> updateSort(@RequestBody List<CourseCategory> categories) {
        courseCategoryService.updateBatchById(categories);
        return Result.success();
    }
}
