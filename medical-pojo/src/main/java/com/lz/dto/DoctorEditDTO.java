package com.lz.dto;

import lombok.Data;

@Data
public class DoctorEditDTO {
    private Integer workStatus;   // 1在线 0不在线
    private String avatar; // 头像链接
}
