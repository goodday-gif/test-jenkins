package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("coach")
public class Coach {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String name;

    private String avatar;

    private String phone;

    private Integer experienceYears;

    private String specialty;

    private String certificate;

    private String introduction;

    private String teachingStyle;

    /** 状态 0-离职 1-在职 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
