package com.lz.controller.user;

import com.lz.app.MedicalApp;
import com.lz.dto.ChatConfigDTO;
import com.lz.result.Result;
import com.lz.service.ConsultationService;
import com.lz.service.MedicalAppService;
import com.lz.vo.AiResultVO;
import com.lz.vo.MedicalReportVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RequestMapping("/user/ai")
@RestController
@Slf4j
public class AiController {

    @Resource
    private MedicalAppService medicalAppService;

    @Resource
    private ConsultationService consultationService;


    /**
     * 流式对话接口
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Transactional
    public Flux<AiResultVO> streamChat(@RequestBody ChatConfigDTO chatConfigDTO) {
        Long chatId = chatConfigDTO.getChatId();
        String message = chatConfigDTO.getMessage();
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

        return medicalAppService.doChatByStream(chatConfigDTO)
                .map(response -> {
                    AssistantMessage output = response.getResult().getOutput();

                    // 实时提取流片段
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

    /**
     * 多模态流式对话接口（图片+文本）
     * 包含：数据库保存、<think>标签拼接、空包过滤
     */
    @PostMapping(value = "/stream/images", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Transactional
    public Flux<AiResultVO> streamChatWithImages(@RequestBody ChatConfigDTO chatConfigDTO) {

        Long chatId = chatConfigDTO.getChatId();
        String message = chatConfigDTO.getMessage();
        List<String> imageUrls = chatConfigDTO.getImageUrls();

        // ==========================================
        // 1. ✅ 先把【用户的提问】保存到数据库
        // ==========================================
        try {
            // msgType: 2 表示 "图片+文本" (根据你的业务约定调整，纯文本通常是1)
            for (String imageUrl : imageUrls) {
                consultationService.saveMessage(chatId, "USER", imageUrl, 2);
            }
            consultationService.saveMessage(chatId, "USER", message, 1);
        } catch (Exception e) {
            log.error("保存用户消息失败", e);
        }

        // 准备容器，分别累积“思考”和“回答”
        StringBuilder fullAnswerBuilder = new StringBuilder();   // 存正文
        StringBuilder fullThinkingBuilder = new StringBuilder(); // 存思考过程

        return medicalAppService.doChatWithImagesByStream(chatConfigDTO)
                // 🔥🔥 核心保护：过滤掉 Spring AI 解析出来的空帧（防止 NPE）🔥🔥
                .filter(response -> response.getResult() != null)

                .map(response -> {
                    AssistantMessage output = response.getResult().getOutput();

                    // 1) 思考内容（适配 DeepSeek/Qwen 的 reasoningContent）
                    String thinkingPart = (String) output.getMetadata()
                            .getOrDefault("reasoningContent", "");

                    // 2) 正文回答
                    String answerPart = output.getText();

                    // 分别累加
                    if (answerPart != null) {
                        fullAnswerBuilder.append(answerPart);
                    }
                    if (thinkingPart != null) {
                        fullThinkingBuilder.append(thinkingPart);
                    }

                    // 实时返回给前端（前端可分别展示 thinking / answer）
                    return new AiResultVO(thinkingPart, answerPart);
                })
                // ==========================================
                // 2. ✅ 监听流结束事件：拼接并保存 AI 消息
                // ==========================================
                .doOnComplete(() -> {
                    String finalAnswer = fullAnswerBuilder.toString();
                    String finalThinking = fullThinkingBuilder.toString();

                    log.info("AI多模态回答结束 - SessionId: {}", chatId);

                    // 🔥🔥 核心逻辑：拼接 <think> 标签 🔥🔥
                    String contentToSave;
                    if (finalThinking != null && !finalThinking.isEmpty()) {
                        // 只有当真的有思考内容时，才拼接标签
                        contentToSave = "<think>\n" + finalThinking + "\n</think>\n" + finalAnswer;
                    } else {
                        // 如果模型没思考（比如 qwen-vl-max 关闭了思考），直接存正文
                        contentToSave = finalAnswer;
                    }

                    // 打印最终保存的内容用于调试
                    // log.info("Saving content: {}", contentToSave);

                    // 异步保存 AI 的回答 (msgType=2 表示图片模式)
                    try {
                        consultationService.saveMessage(chatId, "AI", contentToSave, 1);
                    } catch (Exception e) {
                        log.error("保存AI消息失败", e);
                    }
                })
                // 指定在弹性线程池执行后续操作，避免阻塞 IO 线程
                .publishOn(Schedulers.boundedElastic());
    }




    /**
     * 生成健康报告接口 (非流式，一次性返回 JSON)
     * 前端调用时需要等待 AI 生成完毕
     */
    @PostMapping("/report")
    public Result<MedicalReportVO> generateReport(@RequestBody ChatConfigDTO chatConfigDTO) {
        Long chatId = chatConfigDTO.getChatId();
        String message = chatConfigDTO.getMessage();

        log.info("开始生成健康报告 - ChatId: {}", chatId);

        try {
            // 2. 调用 MedicalApp 中的方法
            // 注意：这里需要确保 medicalApp 是注入进来的 Bean，并且包含 doChatWithReport 方法
            MedicalReportVO report = medicalAppService.doChatWithReport(message, String.valueOf(chatId));

            return Result.success(report);

        } catch (Exception e) {
            log.error("生成报告失败", e);
            return Result.error("报告生成失败，请稍后重试");
        }
    }
}