package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("review")
public class Review {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 评价对象类型：1=课程 2=教练 3=器材 */
    private Integer targetType;

    private Long targetId;

    private Long memberId;

    /** 评分：1-5星 */
    private Integer rating;

    private String content;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    /** 非数据库字段 - 会员昵称 */
    @TableField(exist = false)
    private String memberName;

    /** 非数据库字段 - 会员头像 */
    @TableField(exist = false)
    private String memberAvatar;
}
