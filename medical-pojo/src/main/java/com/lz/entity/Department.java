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
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 科室名称
     */
    private String name;

    /**
     * 科室简介
     */
    private String intro;

    /**
     * 创建时间
     * (插入时自动填充)
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     * (插入和更新时自动填充)
     */
    private LocalDateTime updateTime;
}
