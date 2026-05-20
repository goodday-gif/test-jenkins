package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("class_reserve")
public class ClassReserve {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;

    private Long classId;

    private LocalDateTime reserveTime;

    /** 签到状态 0-未签到 1-已签到 */
    private Integer checkIn;

    /** 状态 0-已取消 1-已预约 2-已完成 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
