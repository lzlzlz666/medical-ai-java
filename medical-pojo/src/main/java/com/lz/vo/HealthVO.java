package com.lz.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HealthVO implements Serializable {

    private LocalDate recordDate;       // 今天日期

    private Integer heartRate;          // bpm
    private BigDecimal glucose;         // mmol/L
    private Integer systolicBp;         // 收缩压
    private Integer diastolicBp;        // 舒张压

}
