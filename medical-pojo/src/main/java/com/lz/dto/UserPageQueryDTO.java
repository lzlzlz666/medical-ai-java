package com.lz.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPageQueryDTO implements Serializable {

    // 页码
    private int page;

    // 每页记录数
    private int pageSize;

    // 搜索关键词 (昵称或账号)
    private String keyword;

    // 账号状态 (1正常 0禁用)
    private Integer status;
}