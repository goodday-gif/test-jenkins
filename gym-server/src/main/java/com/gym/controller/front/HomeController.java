package com.gym.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.Result;
import com.gym.entity.*;
import com.gym.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/front/home")
public class HomeController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private GymClassService gymClassService;

    @Autowired
    private CoachService coachService;

    @Autowired
    private FitnessCourseService fitnessCourseService;

    /**
     * 获取轮播图数据（从公告表获取置顶已发布公告）
     */
    @GetMapping("/banner")
    public Result<List<Announcement>> banner() {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, 1)
               .eq(Announcement::getIsTop, 1)
               .orderByDesc(Announcement::getCreateTime)
               .last("LIMIT 5");
        List<Announcement> list = announcementService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 获取首页统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> stats = new HashMap<>();

        // 会员总数
        long memberCount = memberService.count(new LambdaQueryWrapper<Member>().eq(Member::getStatus, 1));
        // 课程总数
        long classCount = gymClassService.count(new LambdaQueryWrapper<GymClass>().ne(GymClass::getStatus, 0));
        // 教练总数
        long coachCount = coachService.count(new LambdaQueryWrapper<Coach>().eq(Coach::getStatus, 1));
        // 教程总数
        long courseCount = fitnessCourseService.count(new LambdaQueryWrapper<FitnessCourse>().eq(FitnessCourse::getPublishStatus, 1));

        stats.put("memberCount", memberCount);
        stats.put("classCount", classCount);
        stats.put("coachCount", coachCount);
        stats.put("courseCount", courseCount);

        return Result.success(stats);
    }

    /**
     * 获取热门课程（按预约人数排序，前6个）
     */
    @GetMapping("/hot-courses")
    public Result<List<GymClass>> hotCourses() {
        LambdaQueryWrapper<GymClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(GymClass::getStatus, 0)
               .orderByDesc(GymClass::getCurrentCount)
               .last("LIMIT 6");
        List<GymClass> list = gymClassService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 获取热门教程（按播放量排序，前6个）
     */
    @GetMapping("/hot-tutorials")
    public Result<List<FitnessCourse>> hotTutorials() {
        LambdaQueryWrapper<FitnessCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FitnessCourse::getPublishStatus, 1)
               .orderByDesc(FitnessCourse::getPlayCount)
               .last("LIMIT 6");
        List<FitnessCourse> list = fitnessCourseService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 获取最新公告（最近5条已发布公告）
     */
    @GetMapping("/latest-announcements")
    public Result<List<Announcement>> latestAnnouncements() {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, 1)
               .orderByDesc(Announcement::getCreateTime)
               .last("LIMIT 5");
        List<Announcement> list = announcementService.list(wrapper);
        return Result.success(list);
    }
}
