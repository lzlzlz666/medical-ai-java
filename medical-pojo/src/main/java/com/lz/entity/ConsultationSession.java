package com.lz.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;            // 会话ID
    private Long userId;
    private Long doctorId;
    private Integer status;     // 0:AI问诊中 1:医生介入 2:已完成 3:已拒绝
    private String aiSummary;   // AI生成的病情摘要
    private LocalDateTime createTime; // 发起时间
    private LocalDateTime updateTime;
}
