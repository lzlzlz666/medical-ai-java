package com.lz.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DoctorDTO implements Serializable {
    private Long id;
    private String username;    // 登录账号
    private String realName;    // 真实姓名
    private String title;       // 职称
    private Long deptId;        // 科室ID
    private Integer maxDailyAudit; // 每日审核上限
    private String avatar;      // 头像
    private String intro;       // 简介
    private String phone;       // 手机号 (数据库有这个字段，虽然UI没显示输入框，建议加上)
}
