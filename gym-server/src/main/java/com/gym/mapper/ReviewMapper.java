package com.gym.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gym.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    /**
     * 分页查询评价列表，关联会员信息
     */
    @Select("SELECT r.*, m.nickname AS member_name, m.avatar AS member_avatar " +
            "FROM review r LEFT JOIN member m ON r.member_id = m.id " +
            "WHERE r.target_type = #{targetType} AND r.target_id = #{targetId} AND r.deleted = 0 " +
            "ORDER BY r.create_time DESC")
    IPage<Review> selectReviewPage(Page<Review> page, @Param("targetType") Integer targetType, @Param("targetId") Long targetId);

    /**
     * 查询是否已评价（绕过@TableLogic）
     */
    @Select("SELECT * FROM review WHERE target_type = #{targetType} AND target_id = #{targetId} AND member_id = #{memberId} AND deleted = 0 LIMIT 1")
    Review findExistingReview(@Param("targetType") Integer targetType, @Param("targetId") Long targetId, @Param("memberId") Long memberId);
}
