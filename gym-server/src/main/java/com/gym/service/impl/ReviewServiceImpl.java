package com.gym.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gym.common.BusinessException;
import com.gym.entity.Review;
import com.gym.mapper.ReviewMapper;
import com.gym.service.ReviewService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    @Override
    public void addReview(Review review) {
        // 检查评分范围
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            throw new BusinessException("评分必须在1-5之间");
        }
        // 检查是否重复评价
        Review existing = baseMapper.findExistingReview(review.getTargetType(), review.getTargetId(), review.getMemberId());
        if (existing != null) {
            throw new BusinessException("您已经评价过了，不能重复评价");
        }
        this.save(review);
    }

    @Override
    public IPage<Review> getReviewPage(Page<Review> page, Integer targetType, Long targetId) {
        return baseMapper.selectReviewPage(page, targetType, targetId);
    }

    @Override
    public Map<String, Object> getReviewStats(Integer targetType, Long targetId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getTargetType, targetType)
               .eq(Review::getTargetId, targetId);
        long count = this.count(wrapper);

        Double avgRating = 0.0;
        if (count > 0) {
            // 使用列表计算平均分
            LambdaQueryWrapper<Review> ratingWrapper = new LambdaQueryWrapper<>();
            ratingWrapper.eq(Review::getTargetType, targetType)
                         .eq(Review::getTargetId, targetId)
                         .select(Review::getRating);
            java.util.List<Review> reviews = this.list(ratingWrapper);
            double sum = reviews.stream().mapToInt(Review::getRating).sum();
            avgRating = Math.round(sum / count * 10.0) / 10.0;
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("avgRating", avgRating);
        stats.put("count", count);
        return stats;
    }

    @Override
    public void deleteReview(Long reviewId, Long memberId) {
        Review review = this.getById(reviewId);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        if (!review.getMemberId().equals(memberId)) {
            throw new BusinessException("无权删除他人评价");
        }
        this.removeById(reviewId);
    }

    @Override
    public boolean hasReviewed(Integer targetType, Long targetId, Long memberId) {
        Review existing = baseMapper.findExistingReview(targetType, targetId, memberId);
        return existing != null;
    }
}
