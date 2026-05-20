package com.gym.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.BusinessException;
import com.gym.common.PageResult;
import com.gym.common.Result;
import com.gym.entity.Coach;
import com.gym.entity.GymClass;
import com.gym.service.CoachService;
import com.gym.service.GymClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("frontCoachController")
@RequestMapping("/api/front/coach")
public class CoachController {

    @Autowired
    private CoachService coachService;

    @Autowired
    private GymClassService gymClassService;

    /**
     * 教练列表（分页，只返回在职教练）
     */
    @GetMapping("/list")
    public Result<PageResult<Coach>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Coach> page = new Page<>(pageNum, pageSize);
        IPage<Coach> result = coachService.getCoachList(page, null, 1);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 教练详情（包含该教练的课程列表）
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        Coach coach = coachService.getById(id);
        if (coach == null) {
            throw new BusinessException("教练不存在");
        }
        // 获取该教练的课程列表
        LambdaQueryWrapper<GymClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GymClass::getCoachId, id)
               .ne(GymClass::getStatus, 0)
               .orderByDesc(GymClass::getClassTime);
        List<GymClass> classList = gymClassService.list(wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("coach", coach);
        data.put("classList", classList);
        return Result.success(data);
    }
}
