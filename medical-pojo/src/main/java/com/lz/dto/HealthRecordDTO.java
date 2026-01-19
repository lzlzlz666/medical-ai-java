package com.lz.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HealthRecordDTO implements Serializable {
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

}
