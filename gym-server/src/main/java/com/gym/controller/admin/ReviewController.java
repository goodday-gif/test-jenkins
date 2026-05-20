package com.gym.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.common.PageResult;
import com.gym.common.Result;
import com.gym.entity.Review;
import com.gym.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminReviewController")
@RequestMapping("/api/admin/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 查看所有评价（分页，可按类型筛选）
     */
    @GetMapping("/list")
    public Result<PageResult<Review>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer targetType) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(targetType != null, Review::getTargetType, targetType);
        wrapper.orderByDesc(Review::getCreateTime);
        Page<Review> page = new Page<>(pageNum, pageSize);
        IPage<Review> result = reviewService.page(page, wrapper);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    /**
     * 删除评价
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteReview(@PathVariable Long id) {
        reviewService.removeById(id);
        return Result.success();
    }
}
