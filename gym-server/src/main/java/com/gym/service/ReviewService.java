package com.gym.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gym.entity.Review;

import java.util.Map;

public interface ReviewService extends IService<Review> {

    /**
     * 添加评价
     */
    void addReview(Review review);

    /**
     * 分页查询评价列表（关联会员信息）
     */
    IPage<Review> getReviewPage(Page<Review> page, Integer targetType, Long targetId);

    /**
     * 获取评价统计（平均评分、评价数量）
     */
    Map<String, Object> getReviewStats(Integer targetType, Long targetId);

    /**
     * 删除自己的评价
     */
    void deleteReview(Long reviewId, Long memberId);

    /**
     * 检查是否已评价
     */
    boolean hasReviewed(Integer targetType, Long targetId, Long memberId);
}
