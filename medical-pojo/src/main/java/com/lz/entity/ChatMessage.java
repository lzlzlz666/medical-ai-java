package com.lz.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private Long id;
    private Long sessionId;
    private String senderType; // 枚举: USER, DOCTOR, AI, SYSTEM
    private String content;
    private Integer msgType;   // 1:文本 2:图片
    private LocalDateTime createTime;
}
