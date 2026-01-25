package com.lz.config;

import com.lz.properties.AliOssProperties;
import com.lz.rag.MedicalAppDocumentLoader;
import com.lz.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternResolver;

@Configuration
@Slf4j
public class OssConfiguration {

    @Bean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        log.info("开始创建阿里云配置文件信息：{}", aliOssProperties);
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }

    // ✅ 注册 Loader（Bean 名就是方法名 medicalAppDocumentLoader）
    @Bean
    public MedicalAppDocumentLoader medicalAppDocumentLoader(ResourcePatternResolver resolver,
                                                             AliOssProperties p) {
        return new MedicalAppDocumentLoader(
                resolver,
                p.getEndpoint(),
                p.getAccessKeyId(),
                p.getAccessKeySecret(),
                p.getBucketName()
        );
    }
}
