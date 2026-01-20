package com.lz.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorLoginVO implements Serializable {

    private Long id;

    private String username;

    private String realName;

    private String avatar;

    private String token;
}
