package com.lz.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiResultVO {
    private String thinking; // 思考过程
    private String answer;   // 最终回答
}