package com.lz.controller.user;

import com.lz.app.MedicalApp;
import com.lz.service.ConsultationService;
import com.lz.vo.AiResultVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@RequestMapping("/user/ai")
@RestController
@Slf4j
public class AiController {

    @Resource
    private MedicalApp medicalApp;

    @Resource
    private ConsultationService consultationService;

    /**
     * 流式对话接口
     * @param message 用户提问内容
     * @param chatId 会话ID (对应数据库的 session_id)
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Transactional
    public Flux<AiResultVO> streamChat(@RequestParam String message, @RequestParam Long chatId) {

        // ==========================================
        // 1. ✅ 先把【用户的提问】保存到数据库
        // ==========================================
        try {
            // 这里调用你现有的 saveMessage 方法
            // 参数：sessionId, senderType="USER", content=message, msgType=1(文本)
            consultationService.saveMessage(chatId, "USER", message, 1);
        } catch (Exception e) {
            log.error("保存用户消息失败", e);
            // 即使保存失败，也不要阻断后续的 AI 回答，根据业务需求决定是否抛出异常
        }

        // 准备容器，分别累积“思考”和“回答”
        StringBuilder fullAnswerBuilder = new StringBuilder();   // 存正文
        StringBuilder fullThinkingBuilder = new StringBuilder(); // 存思考过程

        return medicalApp.doChatByStream(message, String.valueOf(chatId))
                .map(response -> {
                    AssistantMessage output = response.getResult().getOutput();

                    // 实时提取流片段
                    // 注意：这里取 reasoningContent 的 key 可能因你使用的 AI 框架版本而异
                    // 如果是用 Spring AI Alibaba 整合 DeepSeek，通常在 metadata 里
                    String thinkingPart = (String) output.getMetadata().getOrDefault("reasoningContent", "");
                    String answerPart = output.getText();

                    // 分别累加
                    if (answerPart != null) {
                        fullAnswerBuilder.append(answerPart);
                    }
                    if (thinkingPart != null) {
                        fullThinkingBuilder.append(thinkingPart);
                    }

                    // 返回给前端展示（前端也需要分别展示这两个部分）
                    return new AiResultVO(thinkingPart, answerPart);
                })
                // ==========================================
                // 2. ✅ 监听流结束事件：拼接并保存 AI 消息
                // ==========================================
                .doOnComplete(() -> {
                    String finalAnswer = fullAnswerBuilder.toString();
                    String finalThinking = fullThinkingBuilder.toString();

                    log.info("AI回答结束 - SessionId: {}", chatId);

                    // 🔥🔥 核心逻辑：拼接字符串 🔥🔥
                    // 如果有思考内容，用 <think> 标签包裹，放在最前面
                    String contentToSave;
                    if (finalThinking != null && !finalThinking.isEmpty()) {
                        contentToSave = "<think>\n" + finalThinking + "\n</think>\n" + finalAnswer;
                    } else {
                        contentToSave = finalAnswer;
                    }

                    // 异步保存 AI 的回答
                    try {
                        consultationService.saveMessage(chatId, "AI", contentToSave, 1);
                    } catch (Exception e) {
                        log.error("保存AI消息失败", e);
                    }
                })
                // 指定在弹性线程池执行保存操作，避免阻塞 IO 线程
                .publishOn(Schedulers.boundedElastic());
    }
}