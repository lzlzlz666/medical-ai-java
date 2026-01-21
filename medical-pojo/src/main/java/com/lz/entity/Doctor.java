package com.lz.entity;

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
public class Doctor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID (主键)
     */
    private Long id;

    /**
     * 医生登录工号/账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 头像 URL
     */
    private String avatar;

    /**
     * 所属科室ID，关联 department.id
     */
    private Long deptId;

    /**
     * 职称 (如: 主任医师)
     */
    private String title;

    /**
     * 擅长领域/简介
     */
    private String intro;

    private Integer status;

    /**
     * 每日最大AI审核限额 / 当日剩余被申请次数
     * (根据你的注释，这里似乎是用作减法计数器？或者配置项)
     */
    private Integer maxDailyAudit;

    /**
     * 状态 1:在线 0:离线
     */
    private Integer workStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
