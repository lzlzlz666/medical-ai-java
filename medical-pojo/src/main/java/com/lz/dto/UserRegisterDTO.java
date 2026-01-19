package com.lz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterDTO implements Serializable {

    @NotBlank(message = "用户名不能为空") // 👈 校验：字符串不能为 null 且去除空格后长度大于 0
    private String username;

    @Size(min = 6, message = "密码长度不能少于6位") // 👈 校验：长度限制
    private String password;
}
