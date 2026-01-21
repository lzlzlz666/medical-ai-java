package com.lz.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String realName;

    private String avatar;

    private Long deptId;

    private String deptName; // 科室名字

    private String title;

    private String intro;

    private Integer status;

    private Integer maxDailyAudit;

    private Integer workStatus;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
