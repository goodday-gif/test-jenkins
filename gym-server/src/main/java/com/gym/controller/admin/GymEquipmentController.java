package com.gym.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.PageResult;
import com.gym.common.Result;
import com.gym.entity.GymEquipment;
import com.gym.service.GymEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/equipment")
public class GymEquipmentController {

    @Autowired
    private GymEquipmentService gymEquipmentService;

    @GetMapping("/list")
    public Result<PageResult<GymEquipment>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        Page<GymEquipment> pageParam = new Page<>(page, pageSize);
        IPage<GymEquipment> pageResult = gymEquipmentService.getEquipmentList(pageParam, keyword, category, status);
        return Result.success(new PageResult<>(pageResult.getTotal(), pageResult.getRecords()));
    }

    @GetMapping("/{id}")
    public Result<GymEquipment> detail(@PathVariable Long id) {
        return Result.success(gymEquipmentService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody GymEquipment equipment) {
        gymEquipmentService.save(equipment);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody GymEquipment equipment) {
        gymEquipmentService.updateById(equipment);
        return Result.success();
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        GymEquipment equipment = new GymEquipment();
        equipment.setId(id);
        equipment.setStatus(status);
        gymEquipmentService.updateById(equipment);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        gymEquipmentService.removeById(id);
        return Result.success();
    }
}
