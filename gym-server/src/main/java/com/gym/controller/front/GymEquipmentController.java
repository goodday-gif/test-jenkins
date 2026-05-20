package com.gym.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.BusinessException;
import com.gym.common.PageResult;
import com.gym.common.Result;
import com.gym.entity.GymEquipment;
import com.gym.service.GymEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController("frontGymEquipmentController")
@RequestMapping("/api/front/equipment")
public class GymEquipmentController {

    @Autowired
    private GymEquipmentService gymEquipmentService;

    /**
     * 器材列表（分页，支持 category 筛选，只返回非报废器材）
     */
    @GetMapping("/list")
    public Result<PageResult<GymEquipment>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String category) {
        LambdaQueryWrapper<GymEquipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(GymEquipment::getStatus, 3); // 排除报废
        if (category != null && !category.isEmpty()) {
            wrapper.eq(GymEquipment::getCategory, category);
        }
        wrapper.orderByDesc(GymEquipment::getCreateTime);
        Page<GymEquipment> page = new Page<>(pageNum, pageSize);
        IPage<GymEquipment> result = gymEquipmentService.page(page, wrapper);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 器材详情
     */
    @GetMapping("/{id}")
    public Result<GymEquipment> detail(@PathVariable Long id) {
        GymEquipment equipment = gymEquipmentService.getById(id);
        if (equipment == null) {
            throw new BusinessException("器材不存在");
        }
        return Result.success(equipment);
    }

    /**
     * 获取器材分类列表（distinct category）
     */
    @GetMapping("/categories")
    public Result<List<String>> categories() {
        QueryWrapper<GymEquipment> wrapper = new QueryWrapper<>();
        wrapper.select("DISTINCT category").ne("status", 3);
        List<GymEquipment> list = gymEquipmentService.list(wrapper);
        List<String> categories = list.stream()
                .map(GymEquipment::getCategory)
                .filter(c -> c != null && !c.isEmpty())
                .collect(Collectors.toList());
        return Result.success(categories);
    }
}
