package com.lz.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TodayHealthVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate recordDate;       // 今天日期（可选，但建议带上）

    private Integer heartRate;          // bpm
    private BigDecimal glucose;         // mmol/L
    private Integer systolicBp;         // 收缩压
    private Integer diastolicBp;        // 舒张压

}
