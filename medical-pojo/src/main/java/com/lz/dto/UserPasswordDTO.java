package com.lz.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPasswordDTO implements Serializable {

    private String oldPassword;

    private String newPassword;
}
