package com.lz.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户每日健康记录表
 */
@Data
public class HealthRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 关联的用户ID
     */
    private Long userId;

    /**
     * 心率 (bpm)
     */
    private Integer heartRate;

    /**
     * 血糖 (mmol/L)
     */
    private BigDecimal glucose;

    /**
     * 收缩压 (高压 mmHg)
     */
    private Integer systolicBp;

    /**
     * 舒张压 (低压 mmHg)
     */
    private Integer diastolicBp;

    /**
     * 记录日期（用于判断今日是否已填）
     */
    private LocalDate recordDate;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
