package com.lz.service.impl;

import com.alibaba.cloud.ai.dashscope.chat.MessageFormat;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.lz.advisor.MyLoggerAdvisor;
import com.lz.dto.ChatConfigDTO;
import com.lz.service.MedicalAppService;
import com.lz.vo.MedicalReportVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;

import org.springframework.ai.content.Media;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
// 如果你要用联网搜索 options（按你依赖版本可能是 DashScopeApi.SearchOptions）
// import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;

@Slf4j
@Service
public class MedicalAppServiceImpl implements MedicalAppService {

    @Resource
    private VectorStore medicalAppVectorStore;

    private final ChatClient chatClient;

//    private static final String SYSTEM_PROMPT =
//            "你是医疗方面助手，只能回答慢性病方面,"
//                    + "不要描述你看到的文档结构（比如分割线、标题层级），只回答问题";

    private static final String SYSTEM_PROMPT =
            "你是lz的小助手";

    public MedicalAppServiceImpl(ChatModel dashscopeChatModel) {
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(20)
                .build();

        this.chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        // 记忆 Advisor：自动读写 memory
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        // 你的日志 Advisor（默认启用；如果想“按开关启用”，下面我也写了做法）
                        new MyLoggerAdvisor()
                )
                .build();
    }

    /**
     * AI 基础对话（流式传输），按 ChatConfigDTO 动态：思考/联网/RAG
     */
    public Flux<ChatResponse> doChatByStream(ChatConfigDTO cfg) {
        if (cfg == null) {
            return Flux.error(new IllegalArgumentException("ChatConfigDTO 不能为空"));
        }
        if (cfg.getMessage() == null || cfg.getMessage().isBlank()) {
            return Flux.error(new IllegalArgumentException("message 不能为空"));
        }
        if (cfg.getChatId() == null) {
            return Flux.error(new IllegalArgumentException("chatId 不能为空"));
        }

        // 1) 基础 prompt + conversationId（让 MessageChatMemoryAdvisor 能找到对应会话）
        var promptSpec = chatClient.prompt()
                .user(cfg.getMessage())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, cfg.getChatId()));

        // 2) 按开关决定是否挂载 RAG Advisor
        if (cfg.isEnableRAG()) {
            promptSpec = promptSpec.advisors(new QuestionAnswerAdvisor(medicalAppVectorStore));
        }

        // 3) 动态拼接 DashScope options
        var optionsBuilder = DashScopeChatOptions.builder()
                .withEnableThinking(cfg.isEnableDeepThinking());

        promptSpec = promptSpec.options(optionsBuilder.build());

        // 4) 流式返回
        return promptSpec.stream().chatResponse();
    }

    /**
     * ✅ 多模态流式对话（Stream 版本）
     * 返回类型必须是 Flux<ChatResponse> 以适配 Controller
     */
    public Flux<ChatResponse> doChatWithImagesByStream(ChatConfigDTO cfg) {
        // 1. 基础校验
        if (cfg == null || cfg.getChatId() == null) {
            return Flux.error(new IllegalArgumentException("参数不能为空"));
        }

        // 2. 准备 Media (为了本地调试稳定，继续使用 FileSystemResource)
        List<Media> mediaList = new ArrayList<>();
        try {
            // 🚨 本地调试专用：直接读 D 盘图片
            // 上线时请改回 new UrlResource(url)
            String localPath = "D:\\test.png";
            FileSystemResource fileResource = new FileSystemResource(localPath);

            if (fileResource.exists()) {
                mediaList.add(new Media(MimeTypeUtils.IMAGE_PNG, fileResource));
            } else {
                // 兼容逻辑：如果本地没文件，尝试读 URL
                if (cfg.getImageUrls() != null) {
                    for (String url : cfg.getImageUrls()) {
                        mediaList.add(new Media(MimeTypeUtils.IMAGE_PNG, new UrlResource(url)));
                    }
                }
            }
        } catch (Exception e) {
            log.error("图片加载失败", e);
            return Flux.error(new RuntimeException("图片加载失败: " + e.getMessage()));
        }

        // 3. 构造 UserMessage
        UserMessage userMessage;
        if (!mediaList.isEmpty()) {
            userMessage = UserMessage.builder()
                    .text(cfg.getMessage())
                    .media(mediaList)
                    .metadata(new HashMap<>()) // ✅ 必须初始化 Map
                    .build();

            // ✅✅✅ 关键修复：显式标记消息格式 (解决阿里云 400 问题)
            userMessage.getMetadata().put(DashScopeApiConstants.MESSAGE_FORMAT, MessageFormat.IMAGE);
        } else {
            userMessage = new UserMessage(cfg.getMessage());
        }

        // 4. 配置模型 (Qwen-VL-Max)
        var optionsBuilder = DashScopeChatOptions.builder()
                .withModel("qwen3-vl-flash-2026-01-22") // 推荐用 Max
                .withMultiModel(true)     // 开启多模态
                .withEnableThinking(cfg.isEnableDeepThinking()); // 视觉模型暂时关闭思考

        // 6. 构建 Prompt
        var promptSpec = chatClient.prompt()
                .messages(userMessage)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, cfg.getChatId()))
                .options(optionsBuilder.build());

        // 2) 按开关决定是否挂载 RAG Advisor
        if (cfg.isEnableRAG()) {
            promptSpec = promptSpec.advisors(new QuestionAnswerAdvisor(medicalAppVectorStore));
        }

        // 7. 返回流 (Flux)
        return promptSpec.stream().chatResponse();
    }

    /**
     * AI 健康报告功能（实战结构化输出）
     *
     * @param message
     * @param chatId
     * @return
     */
    public MedicalReportVO doChatWithReport(String message, String chatId) {
        MedicalReportVO medicalReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成健康报告，标题根据用户提问生成，内容为建议列表（4-5条），最终生成健康得分（满分100）")
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .entity(MedicalReportVO.class);
        log.info("loveReport: {}", medicalReport);
        return medicalReport;
    }
}
