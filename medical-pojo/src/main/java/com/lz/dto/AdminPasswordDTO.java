package com.lz.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminPasswordDTO implements Serializable {

    private String oldPassword;

    private String newPassword;
}
