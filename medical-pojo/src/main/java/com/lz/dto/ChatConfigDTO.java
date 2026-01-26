package com.lz.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatConfigDTO {
    private String message;
    private Long chatId;

    private List<String> imageUrls;

    // === 功能开关 ===
    @Builder.Default
    private boolean enableDeepThinking = false; // 是否开启深度思考 (R1)

    @Builder.Default
    private boolean enableRAG = true;           // 是否开启知识库(RAG)
}