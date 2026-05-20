package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("fitness_course")
public class FitnessCourse {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long categoryId;

    private String title;

    private String coverImg;

    private String videoUrl;

    private String content;

    /** 难度 1-入门 2-初级 3-中级 4-高级 */
    private Integer difficulty;

    /** 时长(分钟) */
    private Integer duration;

    private Integer playCount;

    private Integer likeCount;

    private Integer collectCount;

    private String suitableFor;

    private String coachTip;

    /** 发布状态 0-草稿 1-已发布 2-已下架 */
    private Integer publishStatus;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
