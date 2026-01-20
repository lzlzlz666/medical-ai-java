package com.lz.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DoctorLoginDTO implements Serializable {

    private String username;

    private String password;
}
