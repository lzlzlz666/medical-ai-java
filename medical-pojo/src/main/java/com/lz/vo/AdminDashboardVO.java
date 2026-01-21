package com.lz.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardVO implements Serializable {

    // === 顶部卡片数据 ===
    private Integer doctorCount; // 医生总数
    private Integer deptCount;   // 科室总数

    // === 图表数据 (逗号分隔的字符串) ===
    // 日期列表 (X轴)
    private String dateList;

    // 新增用户列表 (Y轴1)
    private String newUserList;

    // 总用户列表 (Y轴2)
    private String totalUserList;
}
