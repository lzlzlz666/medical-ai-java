package com.lz.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class DepartmentPageQueryDTO implements Serializable {
    // 科室名称（模糊查询）
    private String name;

    // 页码
    private int page;

    // 每页记录数
    private int pageSize;
}
