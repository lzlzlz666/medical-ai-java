package com.lz.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;

@Data
@AllArgsConstructor
public class RagFileInfoVO {
    private String key;        // OSS object key，例如 rag/慢性病知识库.md
    private String filename;   // 文件名，例如 慢性病知识库.md
    private String url;        // 公网 URL
    private long size;         // 字节大小
    private Date lastModified; // 最后修改时间
}
