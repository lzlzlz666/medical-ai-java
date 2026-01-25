package com.lz.rag;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MedicalAppDocumentLoader {

    private final ResourcePatternResolver resourcePatternResolver;

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    public MedicalAppDocumentLoader(ResourcePatternResolver resourcePatternResolver,
                                    String endpoint,
                                    String accessKeyId,
                                    String accessKeySecret,
                                    String bucketName) {
        this.resourcePatternResolver = resourcePatternResolver;
        this.endpoint = endpoint;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.bucketName = bucketName;
    }


    public List<Document> loadMarkdowns() {
        List<Document> allDocuments = new ArrayList<>();

        // 1. 初始化 OSS 客户端
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 2. 列出 rag/ 目录下的所有文件
            ObjectListing objectListing = ossClient.listObjects(bucketName, "rag/");
            List<OSSObjectSummary> sums = objectListing.getObjectSummaries();

            for (OSSObjectSummary s : sums) {
                // 过滤掉目录本身，且只处理 .md 文件
                if (s.getKey().endsWith("/") || !s.getKey().endsWith(".md")) {
                    continue;
                }

                // 3. 拼接文件的公网访问 URL
                // 格式通常是: https://{bucket}.{endpoint}/{key}
                // 注意：endpoint 需要去掉 http:// 前缀如果配置里有的话，这里简单拼接演示
                String fileUrl = "https://" + bucketName + "." + endpoint + "/" + s.getKey();

                log.info("发现远程文档: {}", fileUrl);

                // 4. 加载资源 (同方案一)
                Resource resource = resourcePatternResolver.getResource(fileUrl);

                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", resource.getFilename())
                        .build();

                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                allDocuments.addAll(reader.get());
            }
        } catch (Exception e) {
            log.error("OSS 文档加载失败", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return allDocuments;
    }
}

