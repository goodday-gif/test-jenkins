package com.gym.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Orders {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long memberId;

    /** 订单类型 1-课程购买 2-会员充值 3-会员续费 */
    private Integer orderType;

    private BigDecimal amount;

    private Long classId;

    /** 支付状态 0-未支付 1-已支付 2-已退款 */
    private Integer payStatus;

    private LocalDateTime payTime;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
