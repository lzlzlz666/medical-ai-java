package com.lz.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "lz.jwt")
@Data
public class JwtProperties {

    /**
     * 用户生成jwt令牌相关配置
     */
    // 用户的jwt
    private String userSecretKey;
    private long userTtl;
    private String userTokenName;

    // 管理员的jwt
    private String adminSecretKey;
    private long adminTtl;
    private String adminTokenName;

    // 医生的jwt
    private String doctorSecretKey;
    private long doctorTtl;
    private String doctorTokenName;
}
