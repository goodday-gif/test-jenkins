package com.gym.controller.front;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.BusinessException;
import com.gym.common.PageResult;
import com.gym.common.Result;
import com.gym.entity.Review;
import com.gym.security.LoginUser;
import com.gym.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController("frontReviewController")
@RequestMapping("/api/front/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 发表评价（需登录）
     */
    @PostMapping
    public Result<Void> addReview(@RequestBody Map<String, Object> body) {
        Long memberId = getCurrentMemberId();
        Integer targetType = (Integer) body.get("targetType");
        Long targetId = Long.valueOf(body.get("targetId").toString());
        Integer rating = (Integer) body.get("rating");
        String content = (String) body.get("content");

        if (targetType == null || targetId == null || rating == null) {
            throw new BusinessException("参数不完整");
        }

        Review review = new Review();
        review.setTargetType(targetType);
        review.setTargetId(targetId);
        review.setMemberId(memberId);
        review.setRating(rating);
        review.setContent(content);
        reviewService.addReview(review);
        return Result.success();
    }

    /**
     * 获取评价列表（公开）
     */
    @GetMapping("/list")
    public Result<PageResult<Review>> list(
            @RequestParam Integer targetType,
            @RequestParam Long targetId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Review> page = new Page<>(pageNum, pageSize);
        IPage<Review> result = reviewService.getReviewPage(page, targetType, targetId);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 获取评价统计（公开）
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats(
            @RequestParam Integer targetType,
            @RequestParam Long targetId) {
        return Result.success(reviewService.getReviewStats(targetType, targetId));
    }

    /**
     * 删除自己的评价（需登录）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteReview(@PathVariable Long id) {
        Long memberId = getCurrentMemberId();
        reviewService.deleteReview(id, memberId);
        return Result.success();
    }

    /**
     * 检查当前用户是否已评价（公开，未登录返回false）
     */
    @GetMapping("/check")
    public Result<Boolean> check(
            @RequestParam Integer targetType,
            @RequestParam Long targetId) {
        try {
            Long memberId = getCurrentMemberId();
            return Result.success(reviewService.hasReviewed(targetType, targetId, memberId));
        } catch (BusinessException e) {
            return Result.success(false);
        }
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
