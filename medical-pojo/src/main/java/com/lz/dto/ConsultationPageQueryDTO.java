package com.lz.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class ConsultationPageQueryDTO implements Serializable {

    @NotNull(message = "日期不能为空")
    private LocalDate queryDate; // 对应右上角的日期选择器

    private Integer status; // 状态：null-全部，1-待处理，2-已处理

    // 页码
    private int page;

    // 每页记录数
    private int pageSize;
}
