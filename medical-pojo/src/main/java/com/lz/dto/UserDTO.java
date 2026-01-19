package com.lz.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class UserDTO implements Serializable {

    private String nickname;

    private String avatar;

    private String phone;

    private Integer age;

    private Integer gender; // 性别

    private BigDecimal height; // 使用 BigDecimal 对应数据库的 DECIMAL(5,2)

    private BigDecimal weight;

    private Integer isSmoker;

    private Integer isDrinker;
}
