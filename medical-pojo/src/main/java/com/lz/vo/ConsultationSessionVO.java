package com.lz.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ConsultationSessionVO implements Serializable {
    private Long id;            // 会话ID
    private Integer status;     // 0:AI问诊中 1:医生介入 2:已完成 3:已拒绝
    private String aiSummary;   // AI生成的病情摘要
    private LocalDateTime createTime; // 发起时间

    // --- 关联查询出的医生信息 ---
    private Long doctorId;
    private String doctorName;  // 医生姓名
    private String doctorAvatar;// 医生头像
    private String deptName;    // 科室名称
    private String title;       // 职称
}
