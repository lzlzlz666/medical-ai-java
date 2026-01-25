package com.lz.controller.admin;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.lz.constant.MessageConstant;
import com.lz.result.Result;
import com.lz.utils.AliOssUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController("adminCommonRestController")
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    public CommonController(AliOssUtil aliOssUtil) {
        this.aliOssUtil = aliOssUtil;
    }

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            String objectName = UUID.randomUUID().toString() + extension;
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.info("文件上传失败：{}", e);
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }

    /**
     * 【新增】RAG 知识库文档上传
     * 1. 限制只能上传 .md 文件
     * 2. 上传到 rag/ 目录下
     * 3. 保留原始文件名以便知识库识别
     */
    @PostMapping("/upload/rag")
    public Result<String> uploadRagMd(MultipartFile file) {
        log.info("RAG文档上传：{}", file.getOriginalFilename());

        try {
            String originalFilename = file.getOriginalFilename();

            // 1. 基础校验
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".md")) {
                return Result.error("仅支持上传 Markdown (.md) 格式文件");
            }

            // 3. 调用工具类上传
            String filePath = aliOssUtil.uploadRag(file.getBytes(), originalFilename);

            return Result.success(filePath);
        } catch (IOException e) {
            log.error("RAG文档上传失败：{}", e.getMessage());
            return Result.error("上传失败，请重试");
        }
    }

    // =========================
    // 【新增】查询所有 rag/ 下 md
    // =========================
    @GetMapping("/rag/mds")
    public Result<List<RagFileInfo>> listRagMarkdowns() {

        // ⚠️ 这里直接用 aliOssUtil 里的配置字段（需要这些字段有 getter，或者你把它们设为 public）
        String endpoint = normalizeEndpoint(aliOssUtil.getEndpoint());
        String accessKeyId = aliOssUtil.getAccessKeyId();
        String accessKeySecret = aliOssUtil.getAccessKeySecret();
        String bucketName = aliOssUtil.getBucketName();

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            ObjectListing objectListing = ossClient.listObjects(bucketName, "rag/");
            List<OSSObjectSummary> sums = objectListing.getObjectSummaries();

            List<RagFileInfo> result = new ArrayList<>();
            for (OSSObjectSummary s : sums) {
                String key = s.getKey();
                if (key.endsWith("/") || !key.endsWith(".md")) continue;

                String filename = key.substring(key.lastIndexOf('/') + 1);
                String url = buildPublicUrl(bucketName, endpoint, key);

                result.add(new RagFileInfo(key, filename, url, s.getSize(), s.getLastModified()));
            }

            return Result.success(result);
        } catch (Exception e) {
            log.error("查询 rag/ md 失败", e);
            return Result.error("查询失败");
        } finally {
            ossClient.shutdown();
        }
    }

    // =========================
    // 【新增】删除指定 md（按文件名）
    // =========================
    @DeleteMapping("/rag/mds")
    public Result<String> deleteRagMarkdown(@RequestParam  String filename) {

        // 基础防御：只允许删 rag/ 下的 md
        if (filename == null || filename.isBlank()
                || filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return Result.error("非法文件名");
        }
        if (!filename.toLowerCase().endsWith(".md")) {
            return Result.error("仅允许删除 .md 文件");
        }

        String endpoint = normalizeEndpoint(aliOssUtil.getEndpoint());
        String accessKeyId = aliOssUtil.getAccessKeyId();
        String accessKeySecret = aliOssUtil.getAccessKeySecret();
        String bucketName = aliOssUtil.getBucketName();

        String objectKey = "rag/" + filename;

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            ossClient.deleteObject(bucketName, objectKey);
            return Result.success("deleted: " + objectKey);
        } catch (Exception e) {
            log.error("删除 rag md 失败: {}", objectKey, e);
            return Result.error("删除失败");
        } finally {
            ossClient.shutdown();
        }
    }

    // ========== 工具方法 ==========

    private String normalizeEndpoint(String ep) {
        if (ep == null) return "";
        ep = ep.trim();
        if (ep.startsWith("https://")) ep = ep.substring("https://".length());
        if (ep.startsWith("http://")) ep = ep.substring("http://".length());
        return ep;
    }

    private String buildPublicUrl(String bucketName, String endpoint, String objectKey) {
        return "https://" + bucketName + "." + endpoint + "/" + objectKey;
    }

    // ========== 内部返回对象（不单独建 VO/DTO 文件） ==========
    @Data
    public static class RagFileInfo {
        private String key;
        private String filename;
        private String url;
        private long size;
        private Date lastModified;

        public RagFileInfo(String key, String filename, String url, long size, Date lastModified) {
            this.key = key;
            this.filename = filename;
            this.url = url;
            this.size = size;
            this.lastModified = lastModified;
        }
    }
}
