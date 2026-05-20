package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("gym_class")
public class GymClass {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long coachId;

    /** 课程类型 1-团课 2-私教课 3-小班课 */
    private Integer classType;

    private LocalDateTime classTime;

    private String location;

    private Integer maxCapacity;

    private Integer currentCount;

    private BigDecimal price;

    private String description;

    private String coverImg;

    /** 状态 0-已取消 1-未开始 2-进行中 3-已结束 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
