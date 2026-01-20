package com.lz.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===== 用户基本信息（对应个人信息管理区域）=====
    private String nickname;

    private String phone;

    private Integer age;

    private Integer gender;       // 1男 2女（按你项目约定）

    private BigDecimal height;    // cm（前端显示：身高(cm)）
    private BigDecimal weight;    // kg（前端显示：体重(kg)）

    private Integer isSmoker;     // 0否 1是（或你自定义）
    private Integer isDrinker;    // 0否 1是

    private String avatar;        // 头像 URL

    // ===== 今日健康数据（对应“每日健康指标录入”区域）=====
    private HealthVO todayHealth;

    // ===== 今日是否完成（右侧“今日未完成/已完成”）=====
    private Boolean todayCompleted;
}
