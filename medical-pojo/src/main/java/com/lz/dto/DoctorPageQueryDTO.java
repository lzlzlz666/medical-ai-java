package com.lz.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DoctorPageQueryDTO implements Serializable {

    private String realName;

    private Integer deptId;

    private Integer status;

    private Integer workStatus;

    //页码
    private int page;

    //每页显示记录数
    private int pageSize;
}
