package com.gym.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.Result;
import com.gym.entity.ClassReserve;
import com.gym.service.ClassReserveService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/admin/reserve")
public class ClassReserveController {

    @Resource
    private ClassReserveService classReserveService;

    @GetMapping("/list")
    public Result<IPage<ClassReserve>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Integer status) {
        Page<ClassReserve> page = new Page<>(current, size);
        IPage<ClassReserve> result = classReserveService.getReserveList(page, classId, memberId, status);
        return Result.success(result);
    }

    @PutMapping("/check-in/{id}")
    public Result<Void> checkIn(@PathVariable Long id) {
        classReserveService.checkIn(id);
        return Result.success();
    }

    @PutMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        classReserveService.cancelReserve(id);
        return Result.success();
    }
}
