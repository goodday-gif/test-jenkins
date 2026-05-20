package com.gym.controller.admin;

import com.gym.common.Result;
import com.gym.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        return Result.success(statisticsService.getOverview());
    }

    @GetMapping("/member-growth")
    public Result<List<Map<String, Object>>> memberGrowth(
            @RequestParam(required = false, defaultValue = "7") Integer days) {
        return Result.success(statisticsService.getMemberGrowth(days));
    }

    @GetMapping("/revenue")
    public Result<List<Map<String, Object>>> revenue(
            @RequestParam(required = false, defaultValue = "day") String type) {
        return Result.success(statisticsService.getRevenue(type));
    }

    @GetMapping("/course-reserve")
    public Result<List<Map<String, Object>>> courseReserve() {
        return Result.success(statisticsService.getCourseReserve());
    }

    @GetMapping("/course-play")
    public Result<List<Map<String, Object>>> coursePlayTop10() {
        return Result.success(statisticsService.getCoursePlayTop10());
    }
}
