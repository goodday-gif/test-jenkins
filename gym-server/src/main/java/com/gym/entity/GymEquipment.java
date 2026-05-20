package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("gym_equipment")
public class GymEquipment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String category;

    private String image;

    private String functionDesc;

    private String usageMethod;

    private String location;

    /** 状态 1-正常 2-维修 3-报废 */
    private Integer status;

    private LocalDate purchaseTime;

    private LocalDate maintainTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
