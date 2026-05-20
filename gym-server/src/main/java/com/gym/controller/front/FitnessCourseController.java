package com.gym.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.BusinessException;
import com.gym.common.PageResult;
import com.gym.common.Result;
import com.gym.entity.CourseCategory;
import com.gym.entity.CourseInteract;
import com.gym.entity.FitnessCourse;
import com.gym.security.LoginUser;
import com.gym.mapper.CourseInteractMapper;
import com.gym.service.CourseCategoryService;
import com.gym.service.CourseInteractService;
import com.gym.service.FitnessCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("frontFitnessCourseController")
@RequestMapping("/api/front/tutorial")
public class FitnessCourseController {

    @Autowired
    private FitnessCourseService fitnessCourseService;

    @Autowired
    private CourseCategoryService courseCategoryService;

    @Autowired
    private CourseInteractService courseInteractService;

    @Autowired
    private CourseInteractMapper courseInteractMapper;

    /**
     * 教程列表（分页，支持筛选，只返回已发布的）
     */
    @GetMapping("/list")
    public Result<PageResult<FitnessCourse>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer difficulty) {
        LambdaQueryWrapper<FitnessCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FitnessCourse::getPublishStatus, 1);
        wrapper.eq(categoryId != null, FitnessCourse::getCategoryId, categoryId);
        wrapper.eq(difficulty != null, FitnessCourse::getDifficulty, difficulty);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(FitnessCourse::getTitle, keyword);
        }
        wrapper.orderByDesc(FitnessCourse::getCreateTime);
        Page<FitnessCourse> page = new Page<>(pageNum, pageSize);
        IPage<FitnessCourse> result = fitnessCourseService.page(page, wrapper);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 获取教程分类列表（状态正常的）
     */
    @GetMapping("/categories")
    public Result<List<CourseCategory>> categories() {
        LambdaQueryWrapper<CourseCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseCategory::getStatus, 1)
               .orderByAsc(CourseCategory::getSort);
        return Result.success(courseCategoryService.list(wrapper));
    }

    /**
     * 教程详情（同时增加播放量，并返回当前用户互动状态）
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        FitnessCourse course = fitnessCourseService.getById(id);
        if (course == null || course.getPublishStatus() != 1) {
            throw new BusinessException("教程不存在或已下架");
        }
        // 增加播放量
        fitnessCourseService.update(new LambdaUpdateWrapper<FitnessCourse>()
                .eq(FitnessCourse::getId, id)
                .setSql("play_count = play_count + 1"));
        course.setPlayCount(course.getPlayCount() != null ? course.getPlayCount() + 1 : 1);

        // 构建返回结果，包含课程信息 + 互动状态
        Map<String, Object> result = new HashMap<>();
        result.put("course", course);
        result.put("liked", false);
        result.put("collected", false);
        result.put("likeInteractId", null);
        result.put("collectInteractId", null);

        // 如果用户已登录，查询互动状态
        try {
            Long memberId = getCurrentMemberId();
            CourseInteract likeInteract = courseInteractMapper.findByMemberAndCourseAndType(memberId, id, 1);
            if (likeInteract != null && likeInteract.getDeleted() == 0) {
                result.put("liked", true);
                result.put("likeInteractId", likeInteract.getId());
            }
            CourseInteract collectInteract = courseInteractMapper.findByMemberAndCourseAndType(memberId, id, 2);
            if (collectInteract != null && collectInteract.getDeleted() == 0) {
                result.put("collected", true);
                result.put("collectInteractId", collectInteract.getId());
            }
        } catch (BusinessException e) {
            // 未登录，忽略
        }
        return Result.success(result);
    }

    /**
     * 点赞/取消点赞教程（Toggle模式，需登录）
     */
    @PostMapping("/{id}/like")
    public Result<Map<String, Object>> like(@PathVariable Long id) {
        Long memberId = getCurrentMemberId();
        return Result.success(toggleInteract(memberId, id, 1, "like_count"));
    }

    /**
     * 收藏/取消收藏教程（Toggle模式，需登录）
     */
    @PostMapping("/{id}/collect")
    public Result<Map<String, Object>> collect(@PathVariable Long id) {
        Long memberId = getCurrentMemberId();
        return Result.success(toggleInteract(memberId, id, 2, "collect_count"));
    }

    /**
     * 切换互动状态（点赞/收藏）
     */
    private Map<String, Object> toggleInteract(Long memberId, Long courseId, int type, String countField) {
        // 用自定义SQL绕过@TableLogic，查询包括已逻辑删除的记录
        CourseInteract existing = courseInteractMapper.findByMemberAndCourseAndType(memberId, courseId, type);
        Map<String, Object> result = new HashMap<>();
        if (existing == null) {
            // 不存在记录 → 新建
            CourseInteract interact = new CourseInteract();
            interact.setMemberId(memberId);
            interact.setCourseId(courseId);
            interact.setType(type);
            interact.setCreateTime(LocalDateTime.now());
            interact.setDeleted(0);
            courseInteractService.save(interact);
            fitnessCourseService.update(new LambdaUpdateWrapper<FitnessCourse>()
                    .eq(FitnessCourse::getId, courseId)
                    .setSql(countField + " = " + countField + " + 1"));
            result.put("active", true);
            result.put("interactId", interact.getId());
        } else if (existing.getDeleted() == 1) {
            // 已逻辑删除 → 恢复
            courseInteractMapper.updateDeletedById(existing.getId(), 0);
            fitnessCourseService.update(new LambdaUpdateWrapper<FitnessCourse>()
                    .eq(FitnessCourse::getId, courseId)
                    .setSql(countField + " = " + countField + " + 1"));
            result.put("active", true);
            result.put("interactId", existing.getId());
        } else {
            // 未删除 → 取消（逻辑删除）
            courseInteractMapper.updateDeletedById(existing.getId(), 1);
            fitnessCourseService.update(new LambdaUpdateWrapper<FitnessCourse>()
                    .eq(FitnessCourse::getId, courseId)
                    .setSql(countField + " = GREATEST(" + countField + " - 1, 0)"));
            result.put("active", false);
            result.put("interactId", existing.getId());
        }
        return result;
    }

    /**
     * 评论教程（需登录）
     */
    @PostMapping("/{id}/comment")
    public Result<Void> comment(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Long memberId = getCurrentMemberId();
        String content = body.get("content");
        if (!StringUtils.hasText(content)) {
            throw new BusinessException("评论内容不能为空");
        }
        CourseInteract interact = new CourseInteract();
        interact.setMemberId(memberId);
        interact.setCourseId(id);
        interact.setType(3);
        interact.setContent(content);
        interact.setCreateTime(LocalDateTime.now());
        interact.setDeleted(0);
        courseInteractService.save(interact);
        return Result.success();
    }

    /**
     * 获取教程评论列表（分页）
     */
    @GetMapping("/{id}/comments")
    public Result<PageResult<CourseInteract>> comments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<CourseInteract> page = new Page<>(pageNum, pageSize);
        IPage<CourseInteract> result = courseInteractService.getCommentList(page, id);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 取消点赞/收藏（需登录）
     */
    @DeleteMapping("/interact/{id}")
    public Result<Void> cancelInteract(@PathVariable Long id) {
        Long memberId = getCurrentMemberId();
        CourseInteract interact = courseInteractService.getById(id);
        if (interact == null) {
            throw new BusinessException("记录不存在");
        }
        if (!interact.getMemberId().equals(memberId)) {
            throw new BusinessException("无权操作");
        }
        courseInteractService.removeById(id);
        // 更新教程对应计数
        if (interact.getType() == 1) {
            fitnessCourseService.update(new LambdaUpdateWrapper<FitnessCourse>()
                    .eq(FitnessCourse::getId, interact.getCourseId())
                    .setSql("like_count = GREATEST(like_count - 1, 0)"));
        } else if (interact.getType() == 2) {
            fitnessCourseService.update(new LambdaUpdateWrapper<FitnessCourse>()
                    .eq(FitnessCourse::getId, interact.getCourseId())
                    .setSql("collect_count = GREATEST(collect_count - 1, 0)"));
        }
        return Result.success();
    }

    private Long getCurrentMemberId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) auth.getPrincipal();
            if ("member".equals(loginUser.getUserType())) {
                Long id = loginUser.getId();
                if (id != null) {
                    return id;
                }
            }
        }
        throw new BusinessException(401, "请先登录");
    }
}
