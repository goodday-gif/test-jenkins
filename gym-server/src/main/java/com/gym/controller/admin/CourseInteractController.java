package com.gym.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.PageResult;
import com.gym.common.Result;
import com.gym.entity.CourseInteract;
import com.gym.service.CourseInteractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/interact")
public class CourseInteractController {

    @Autowired
    private CourseInteractService courseInteractService;

    @GetMapping("/comments")
    public Result<PageResult<CourseInteract>> commentList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long courseId) {
        Page<CourseInteract> pageParam = new Page<>(page, pageSize);
        IPage<CourseInteract> pageResult = courseInteractService.getCommentList(pageParam, courseId);
        return Result.success(new PageResult<>(pageResult.getTotal(), pageResult.getRecords()));
    }

    @DeleteMapping("/comment/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        courseInteractService.removeById(id);
        return Result.success();
    }
}
